package com.zerobase.dividend.persist;

import com.zerobase.dividend.persist.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    // Optional로 감싸는 이유는 null point exception 방지 및 값이 없을때도 처리하기 편하기 때문
    Optional<CompanyEntity> findByName(String companyName);
}
