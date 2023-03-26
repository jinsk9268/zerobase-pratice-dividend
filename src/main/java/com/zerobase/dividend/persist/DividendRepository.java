package com.zerobase.dividend.persist;

import com.zerobase.dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long companyId);

    @Transactional
    void deleteAllByCompanyId(Long id);

    // companyId, date 복합 유닉키 설정해둬서 일반 select where 하는거보다 빠름
    // 인덱스를 걸지않고 데이터를 조회하게되면 db 성능에 지장이 있음
    // 해당 날짜와 회사가 존재하는지 확인
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
}
