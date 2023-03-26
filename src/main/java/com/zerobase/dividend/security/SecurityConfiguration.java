package com.zerobase.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 권한 제어할 어노테이션 쓰기 위해
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 실제 api 관련 경로
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt로 구현했으므로 세션은 stateless
                .and()
                .authorizeRequests()
                .antMatchers("/**/signup", "/**/signin")
                .permitAll() // signup, signin은 무조건적으로 권한 허용
                .and()
                .addFilterBefore( // 필터의 순서
                        this.jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }

    // 개발 관련 경로
    @Override
    public void configure(final WebSecurity web) throws Exception {
        /*
            h2-console 뒤에 모든 경로 허용하고
            h2-console 경로로 시작하는 api 요청의 인증정보는 무시하겠다
            즉 인증관련 정보가 없어도 자유로운 접근을 허하겠다
            ex) 스웨거, h2-console 같은 모든 api 경로에 인증을 필요로하지 않을때 설정
         */
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    // spring boot 2.x 대부터 선언해줘야함
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }
}
