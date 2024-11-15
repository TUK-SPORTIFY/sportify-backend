package com.tuk.sportify.global.response;

import lombok.Builder;

@Builder
public record SuccessResponse<T>(String status, String path, T data) {}