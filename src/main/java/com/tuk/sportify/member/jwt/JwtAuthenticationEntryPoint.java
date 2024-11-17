package com.tuk.sportify.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuk.sportify.member.jwt.token.TokenStatus;
import com.tuk.sportify.member.jwt.token.dto.TokenValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import com.tuk.sportify.member.jwt.token.dto.ApiResponseJson;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.util.Map;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String VALIDATION_RESULT_KEY = "result";
    private static final String ERROR_MESSAGE_KEY = "errMsg";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        TokenValidationResult result = (TokenValidationResult) request.getAttribute(VALIDATION_RESULT_KEY);
        String errorMessage = result.getTokenStatus().getMessage();
        int errorCode;

        switch (result.getTokenStatus()) {
            case TOKEN_EXPIRED -> errorCode = ResponseStatusCode.TOKEN_EXPIRED;
            case TOKEN_IS_BLACKLIST -> errorCode = ResponseStatusCode.TOKEN_IS_BLACKLIST;
            case TOKEN_WRONG_SIGNATURE -> errorCode = ResponseStatusCode.TOKEN_WRONG_SIGNATURE;
            case TOKEN_HASH_NOT_SUPPORTED -> errorCode = ResponseStatusCode.TOKEN_HASH_NOT_SUPPORTED;
            case WRONG_AUTH_HEADER -> errorCode = ResponseStatusCode.NO_AUTH_HEADER;
            default -> {
                errorMessage = TokenStatus.TOKEN_VALIDATION_TRY_FAILED.getMessage();
                errorCode = ResponseStatusCode.TOKEN_VALIDATION_TRY_FAILED;
            }
        }

        sendError(response, errorMessage, errorCode);
    }

    private void sendError(HttpServletResponse response, String msg, int code) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiResponseJson responseJson = new ApiResponseJson(HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED), code, Map.of(ERROR_MESSAGE_KEY, msg)); // 응답할 데이터 생성
        String jsonToString = objectMapper.writeValueAsString(responseJson); // String으로 변환
        response.getWriter().write(jsonToString);
    }
}