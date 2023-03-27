package com.zerobase.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * api 호출시 바로 controller 로 오는게 아니라
 * 필터 -> 서블릿 -> 인터셉터 -> AOP 레이어 -> 컨트롤러 코드 실행
 * 응답값이 나갈때도 바로 컨트롤러에서 응답값이 나가는게 아니라
 * AOP -> 인터셉터 -> 필터를 걸쳐서 응답이 나가게된다
 * OncePerRequestFilter 한 요청당 한번의 필터 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 해더에서 어떤 키로 토큰을 주고받을 것인지 설정
    public static final String TOKEN_HEADER = "Authorization";
    // 인증 타입을 위한 프리픽스 설정, jwt 토큰 사용시 Bearer을 붙인다 (Bearer 토큰~~)
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    // 요청이 들어올때마다 요청에 토큰이 포함되어있는지 확인해서 토큰이 유효한지 아닌지 확인
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        // 토큰 유효성 확인하여 유효하다면 context에 담는다
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 어떤 사용자가 어떤 경로에 접근했는지
            log.info(String.format(
                    "[%s] -> %s",
                    this.tokenProvider.getUsername(token),
                    request.getRequestURI()
            ));
        }

        // 필터가 연속적으로 실행될 수 있도록
        filterChain.doFilter(request, response);
    }

    // request에서 토큰 가져오기
    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
