package com.zerobase.dividend.service;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor // Bean이 생성될 때 사용할 수 있도록
public class CompanyService {
    private final Scraper yahooFiananceScraper;
    public Company save(String ticker) {
        return null;
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFiananceScraper.scrapCompanyByTicker(ticker);

        // company 정보를 스크래핑할 때 오류가 발생(즉 회사 정보 존재X)해서 null을 반환하면 에러 throw
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFiananceScraper.scrap(company);

        // 스크래핑 결과

        return null;
    }
}
