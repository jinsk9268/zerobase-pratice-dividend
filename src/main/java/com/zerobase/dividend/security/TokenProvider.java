package com.zerobase.dividend.security;

import com.zerobase.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final MemberService memberService;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        // 사용자의 권한 정보 저장
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        // 토큰이 생성된 시간
        Date now = new Date();

        // 토큰 만료 시간, var로 선언해도 됨
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        // 위의 정보들로 토큰 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                .compact(); // 스트링 변환
    }

    // 토큰 유효한지 확인하는 메서드들 구현
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        // 토큰이 비어있다면
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = this.parseClaims(token);

        // 토큰의 만료 시간이 현재 시간보다 전인지 왜 !을 붙여 반전?
        return !claims.getExpiration().before(new Date());
    }

    // 토큰으로 부터 클레임 정보를 가져오는 메서드 구현
    private Claims parseClaims(String token) {
        // 토큰 만료시간이 경과됐으면 exception 발생하므로 예외처리하기
        try {
            return Jwts.parser()
                    .setSigningKey(this.secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // jwt로 부터 인증정보 가져오기
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));

        // 스프링이 지원해주는 토큰 형태로 변경
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities()
        );
    }
}
