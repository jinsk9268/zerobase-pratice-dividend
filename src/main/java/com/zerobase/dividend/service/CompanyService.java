package com.zerobase.dividend.service;

import com.zerobase.dividend.exception.impl.NoCompanyException;
import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.DividendRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.persist.entity.DividendEntity;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

// 싱글톤으로 관리, 이 프로그램이 실행되는 동안 CompanyService 인스턴스는 한번만 생성, 한 인스턴스만 사용, 프로그램 전체에서 한개의 인스턴스만 사용되어야할 때
@Service
@AllArgsConstructor // Bean이 생성될 때 사용할 수 있도록
public class CompanyService {
    private final Trie trie; // AppConfig에 Bean으로 등록되어있는 trie가 주입
    private final Scraper yahooFiananceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (this.companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    // 클래서 밖에선 메서드 호출할 수 없음
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
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        // .map - Collection의 element를 다른값으로 맵핑해야될때 사용
        // Dividend 인스턴스 하나하나가 e에 해당
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        // 다건 데이터 저장이므로 saveAll
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        // 개수 제한을 위해 Pageable 추가
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities
                = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null); // 자동완성 기능만 구현할거기때문에 value에는 null
    }

    public List<String> autocomplete(String keyword) {
        // 찾을때는 preFixMap으로 가져온다, keySet을 리스트 형태로 반환
        // 나중에 수가 너무 많아지면 .stream().limit(10)으로 제한을 두거나
        // pageable 사용해서 추가
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        CompanyEntity company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        // companyId로 배당금 정보 지우기
        this.dividendRepository.deleteAllByCompanyId(company.getId());

        // 배당금 정보 다 지운 후 회사도 삭제해야 완전 삭제
        this.companyRepository.delete(company);

        // 자동완성을 위한 트라이에도 회사 명을 지워줘야한다
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
