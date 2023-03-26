package com.zerobase.dividend.web;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.constants.CacheKey;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company") // 공통경로 설정
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return ResponseEntity.ok(
                // 자동완성 Trie
                // this.companyService.autocomplete(keyword)

                // 자동완성 DB LIKE
                this.companyService.getCompanyNamesByKeyword(keyword)
        );
    }

    /**
     * 페이징 기법이 적용된 회사 정보 조회
     * @param pageable : 중간에 변경되지 않도록 final 선언
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);

        return ResponseEntity.ok(companies);
    }

    /**
     * 회사 및 배당금 정보 추가
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')") // 쓰기권한이 있는 유저만 허용, 이넘 프리픽스의 윗부분(ROLE_XXX)
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        // 사용자가 입력한 ticker 값
        String ticker = request.getTicker().trim();

        // 클라이언트가 공백을 입력했을 경우
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);

        // trie에서 데이터를 가져오기위해선 trie에 회사명이 저장되어 있어야 한다
        // 그래서 회사를 추가할때마다 trie에 회사명도 추가
        // this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);

        // 캐쉬에서도 company 삭제해야함
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName );
    }
}
