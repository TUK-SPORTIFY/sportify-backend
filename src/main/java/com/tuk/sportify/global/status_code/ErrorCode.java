package com.tuk.sportify.global.status_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * Sport Voucher : 스포츠 이용권 관련 에러 코드
     */
    SPORT_VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "100", "존재하지 않는 스포츠 이용권입니다."),
    SPORT_VOUCHER_CLOSED(HttpStatus.NOT_FOUND, "101", "해당 이용권은 종료됐습니다."),

    /**
     * Crew : 크루 관련 에러 코드
     */
    CREW_NOT_FOUND(HttpStatus.BAD_REQUEST, "200", "존재하지 않는 크루입니다."),

    /**
     * Member : 멤버 관련 에러 코드
     */
    MEMBER_REGISTER_EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "300", "이미 존재하는 이메일입니다."),
    MEMBER_REGISTER_PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "301", "비밀번호는 최소 8자 이상이며, 숫자, 특수문자, 대소문자를 포함해야 합니다."),
    MEMBER_REGISTER_ETC_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "302", "회원가입 양식에 맞지 않습니다."),

    MEMBER_LOGIN_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "310", "비밀번호가 일치하지 않습니다."),

    MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "320", "존재하지 않는 회원입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "321", "해당 ID의 회원을 찾을 수 없습니다."),
    MEMBER_EMPTY_MEMBER_LIST(HttpStatus.BAD_REQUEST, "322", "회원 목록이 비어 있습니다."),

    /**
     * Jwt : Jwt 관련 에러 코드
     */

    JWT_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "400", "Token expired"),
    JWT_TOKEN_IS_BLACKLIST(HttpStatus.BAD_REQUEST, "401", "Token is blacklisted"),
    JWT_TOKEN_WRONG_SIGNATURE(HttpStatus.BAD_REQUEST, "402", "Token has wrong signature"),
    JWT_TOKEN_HASH_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "403", "Token hash algorithm not supported"),
    JWT_NO_AUTH_HEADER(HttpStatus.BAD_REQUEST, "404", "Authorization header is missing"),
    JWT_TOKEN_VALIDATION_TRY_FAILED(HttpStatus.BAD_REQUEST, "405", "Token validation attempt failed");

    private final HttpStatus httpStatus;
    private final String code;
    private final String msg;
}