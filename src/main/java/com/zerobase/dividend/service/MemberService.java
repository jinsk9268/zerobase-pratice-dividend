package com.zerobase.dividend.service;

import com.zerobase.dividend.model.Auth;
import com.zerobase.dividend.persist.MemberRepository;
import com.zerobase.dividend.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위해

    // 스프링 시큐리티의 기능을 사용하기 위해
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // MemberEntity는 UserDetail을 상속해서 만든 클래스이기 때문에 바로 리턴 가능
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    // 회원 가입 기능 구현
    /*
        1. MemberEntity 먼저 정의, 스프링 시큐리티 사용을 위해 UserDetails 상속
        2. MemberEntity 사용을 위한 MemberRepository 에 필요한 메서드 정의
        3. AppConfig 클래스에서 빈 등록, 사용자 메타정보를 저장할때 패스워드 같은 민감정보는 그대로 저장되지 않도록 엔코더 설정
        4. MemberService 에서 UserDetailsService 를 상속하는 register 메서드 생성
        5. 파라미터로 받은 id 값이 중복된 아이디인지 확인해주고
        6. 중복이 없다면 받은 패스워드는 인코딩해서 디비에 저장
     */
    public MemberEntity register(Auth.SignUp member) {
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());

        if (exists) {
            throw new RuntimeException("이미 사용중인 아이디 입니다.");
        }

        // 인코딩된 password로 password 재설정
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        // 회원정보 저장
        MemberEntity result = this.memberRepository.save(member.toEntity());

        return result;
    }

    // 로그인을 위한 검증
    public MemberEntity authenticate(Auth.SignIn member) {
        // 인풋으로 입력받은 userId를 기반으로 저장된 member을 불러온다 (패스워드는 인코딩된 패스워드가 들어있음)
        MemberEntity user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        // 인코딩된 패스워드끼리 서로 일치하지 않는 경우
        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 정상적으로 일치하는 경우 user 반환
        return user;
    }
}
