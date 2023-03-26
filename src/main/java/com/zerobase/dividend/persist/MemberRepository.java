package com.zerobase.dividend.persist;

import com.zerobase.dividend.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // id를 기준으로 회원정보를 찾기 위해
    Optional<MemberEntity> findByUsername(String username);

    // 이미 존재하는 id인지 확인하기 위해 사용
    boolean existsByUsername(String username);

}
