package com.zerobase.dividend.web;

import com.zerobase.dividend.model.Auth;
import com.zerobase.dividend.persist.entity.MemberEntity;
import com.zerobase.dividend.security.TokenProvider;
import com.zerobase.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입 API
     *
     * @param request
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        return ResponseEntity.ok(
                this.memberService.register(request)
        );
    }
}
