package com.tuk.sportify.member.exception;

import com.tuk.sportify.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmptyMemberListException extends RuntimeException {

    private final ErrorCode errorCode;
}

