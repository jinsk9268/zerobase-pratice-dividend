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

    /**
     * 로그인 API
     *
     * @param request
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 사용자한테 입력받은 아이디, 패스워드 검증
        MemberEntity member = this.memberService.authenticate(request);

        // 일치한다면 jwt 토큰 생성 후 반환
        return ResponseEntity.ok(
                this.tokenProvider.generateToken(member.getUsername(), member.getRoles())
        );
    }
}
