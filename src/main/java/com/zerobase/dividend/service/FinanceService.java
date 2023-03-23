package com.zerobase.dividend.service;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.Dividend;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.DividendRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보를 조회
        // orElseThrow - 원래는 Optional<CompanyEntity>로 반환받아야 하나
        // 값이 없으면 인자로 넘겨주는 값을 보여주고 있으면 Optional이 벗겨진 알맹이 그대로 보여줘
        // CompanyEntity 그대로 반환타입으로 가질 수 있다
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다. -> " + companyName));

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        // ScrapedResult CompanyEntity, DividendEntity 타입이 아닌 우리가 생성한 DTO 타입이므로 변환 필요
        List<Dividend> dividends = new ArrayList<>();
        for (DividendEntity entity : dividendEntities) {
            dividends.add(
                    Dividend.builder()
                            .date(entity.getDate())
                            .dividend(entity.getDividend())
                            .build()
            );
        }

        // stream으로 하는 방법
        // List<Dividend> dividends = dividendEntities.stream()
        //         .map(e -> Dividend.builder()
        //                 .date(e.getDate())
        //                 .dividend(e.getDividend())
        //                 .build())
        //         .collect(Collectors.toList());

        return new ScrapedResult(
                Company.builder()
                        .ticker(company.getTicker())
                        .name(company.getName())
                        .build(),
                dividends
        );
    }
}
