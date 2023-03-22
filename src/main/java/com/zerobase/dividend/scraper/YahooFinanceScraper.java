package com.zerobase.dividend.scraper;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.Dividend;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
    // 값이 변하면 안되기 때문에 상수로 선언
    private static final String STATISTIC_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_DATE = 86400; // 시작날짜는 바꿀필요 없음 -> 60초 * 60분 * 24시간

    @Override
    public ScrapedResult scrap(Company company) {
        // 강의에선 var로 사용
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; // 현재시간을 ms로 받아 초로 바꾼다
            String url = String.format(STATISTIC_URL, company.getTicker(), START_DATE, now);
            // HTTP Connection을 맺는다, 연결을 요청할 URL 넣기
            Connection connection = Jsoup.connect(url);
            // Connection으로부터 HTML 문서를 받아서 파싱된 형태의 Document 인스턴스 반환
            Document document = connection.get();

            // 파싱된 데이터인 Document에서 필요한 데이터 추출
            // 스크래핑할 html 태그 속성, 리스트 형태로 반환, 이 속성을 가진 element가 1개가 아닐 수 있기 때문
            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);
            // table 태그의 자식 중 tbody의 인덱스는 1
            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            // 모든 데이터 순회
            for (Element e : tbody.children()) {
                String txt = e.text();

                // 배당금 정보는 끝이 dividend로 끝난다
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }

            scrapedResult.setDividendEntities(dividends);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapedResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            // Jsoup connection을 통해 Document 가져오기
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            // MSFT - Microsoft Corporation 형태이므로 깔끔하게 출력
            String title = titleEle.text().split(" - ")[1].trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
