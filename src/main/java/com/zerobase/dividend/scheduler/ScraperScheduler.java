package com.zerobase.dividend.scheduler;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor // repository가 초기화 될 수 있도록
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScrapper;

    // 일정 주기마다 수행
    @Scheduled(cron = "")
    public void yahooFinanceScheduling() {
        // 저장된 모든 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사 마다 배당금 정보를 스크래핑
        for (CompanyEntity company : companies) { // var로 선언해도됨
            ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(
                    Company.builder()
                            .name(company.getName())
                            .ticker(company.getTicker())
                            .build()
            );
        }

        // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값만 저장
    }
}
