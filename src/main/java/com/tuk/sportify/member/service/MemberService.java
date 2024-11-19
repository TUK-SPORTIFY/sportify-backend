package com.tuk.sportify.member.service;

import com.tuk.sportify.global.status_code.ErrorCode;
import com.tuk.sportify.member.domain.Member;
import com.tuk.sportify.member.dto.CreateMemberRequest;
import com.tuk.sportify.member.dto.MemberInfoResponse;
import com.tuk.sportify.member.exception.LoginFailedException;
import com.tuk.sportify.member.exception.MemberNotFoundException;
import com.tuk.sportify.member.exception.RegisterFailedException;
import com.tuk.sportify.member.jwt.token.TokenProvider;
import com.tuk.sportify.member.jwt.token.dto.TokenInfo;
import com.tuk.sportify.member.repository.MemberRepository;
import com.tuk.sportify.member.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$"; // 최소 8자리 + 영어, 숫자, 특수문자를 모두 포함해야함.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Member createMember(CreateMemberRequest request) {
        checkPasswordStrength(request.password());

        //이미 등록된 이메일인지 체크
        if (memberRepository.existsByEmail(request.email())) {
            log.info("이미 등록된 이메일={}", request.email());
            throw new RegisterFailedException(ErrorCode.MEMBER_REGISTER_EMAIL_ALREADY_EXIST);
        }

        Member member = memberMapper.CreateMemberRequestToMember(request, passwordEncoder);
        return memberRepository.save(member);
    }

    private void checkPasswordStrength(String password) {
        //비밀번호 정책에 맞는지 체크
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return;
        }

        log.info("비밀번호 정책 미달");
        throw new RegisterFailedException(ErrorCode.MEMBER_REGISTER_PASSWORD_POLICY_VIOLATION);
    }

    public TokenInfo loginMember(String email, String password) {
        try {
            Member member = findMemberByEmail(email);

            checkPassword(password, member);

            return tokenProvider.createToken(member);
        } catch (BadCredentialsException e) {
            throw new LoginFailedException(ErrorCode.MEMBER_LOGIN_PASSWORD_INCORRECT);
        }
    }

    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("비밀번호가 일치하지 않습니다.");
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.info("계정이 존재하지 않습니다.");
            return new LoginFailedException(ErrorCode.MEMBER_NOT_EXIST);
        });
    }

    // 전체 회원 조회
    public List<Member> getAllMembers() {
        return memberRepository.findAll().stream()
                .findAny()
                .map(member -> memberRepository.findAll())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_EMPTY_MEMBER_LIST));
    }


    // ID로 회원 조회
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }
}

