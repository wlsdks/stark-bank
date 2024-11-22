package com.example.cqrs.command.dto;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CreateAccountRequest {

    private String accountId;
    private String userId;

    // factory method
    public static CreateAccountRequest of(String accountId, String userId) {
        return new CreateAccountRequest(accountId, userId);
    }

}