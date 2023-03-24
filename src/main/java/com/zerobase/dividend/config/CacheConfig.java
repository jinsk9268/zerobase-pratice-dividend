package com.zerobase.dividend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // 레디스 서버와의 연결, 커넥션 관리를 위한 bean 초기화
    // redis와 커넥션 맺을 수 있는 커넥션팩토리 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 싱글 인스턴스 서버
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host);
        conf.setPort(this.port);

        // 생성한 redis를 LettuceConnectionfactory의 설정정보로 넣어서 인스턴스 생성
        return new LettuceConnectionFactory(conf);
    }
}
