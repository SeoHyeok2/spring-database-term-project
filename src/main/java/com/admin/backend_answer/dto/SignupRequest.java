package com.admin.backend_answer.dto;

public record SignupRequest(
        String memberName,
        String memberEmail,
        String memberPassword,
        String passportNumber
) {
}
