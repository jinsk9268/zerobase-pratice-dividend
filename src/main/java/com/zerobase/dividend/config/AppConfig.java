package com.zerobase.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    // 이 서비스에서 하나만 유지되어야하고 코드 일관성 유지를위해 bean으로 trie 관리
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }
}
