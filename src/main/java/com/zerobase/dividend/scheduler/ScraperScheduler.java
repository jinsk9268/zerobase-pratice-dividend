package com.zerobase.dividend.scheduler;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.DividendRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.persist.entity.DividendEntity;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor // repository가 초기화 될 수 있도록
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository; // 배당금 정보 저장 위해
    private final Scraper yahooFinanceScrapper;

    // 일정 주기마다 수행
    // value - 레디스 서버 키의 prefix, 앞에서 설정한거 넣어주면됨
    // allEntries - 레디스 캐쉬의 value에 해당하는 모든 키를 지우겠다
    // 모두 지우지 않으려면 key = "" 와 같이 특정 키를 넣으면 됨
    // 그래서 스케줄러 동작할 때마다 캐시도 비워짐, 나중에 다시 검색 시 캐시 저장
    @CacheEvict(value = "finance", allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}") // 매일 정각 실행
    public void yahooFinanceScheduling() {
        log.info("scrapping scheduler is started");
        // 저장된 모든 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사 마다 배당금 정보를 스크래핑
        for (CompanyEntity company : companies) { // var로 선언해도됨
            // 적절한 로그를 남기는 습관 중요함
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(
                    new Company(company.getName(), company.getTicker())
            );

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값만 저장
            scrapedResult.getDividends().stream()
                    // dividend 모델을 dividend Entity로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // DividendEntity를 하나씩 DividendRepository에 삽입, 단 존재하지 않는 값만
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            // 안그러면 요청한 서버에 과부하가 걸리기 때문
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { // 인터럽트 받는 스레드가 blocking 될 수 있는 메소드 실행할때 발생
                e.printStackTrace();
                // 단순히 메시지만 출력하고 종료는 적절한 처리가 아니므로 현재쓰레드 인터럽트 처리
                Thread.currentThread().interrupt();
            }

        }
    }
}
