package com.admin.backend_answer.dto;

public record LoginRequest(
        String email,
        String password
) {
}
