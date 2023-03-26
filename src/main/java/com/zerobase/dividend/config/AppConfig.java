package com.zerobase.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    // 이 서비스에서 하나만 유지되어야하고 코드 일관성 유지를위해 bean으로 trie 관리
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }

    // 어떤 패스워드 타입으로 엔코더해줄건지 config에서 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
