package com.zerobase.dividend.model;

import com.zerobase.dividend.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {
    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        // MemberEntity를 생성하는 메서드 추가
        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

    @Data
    public static class SignIn {
        private String username;
        private String password;
    }
}
