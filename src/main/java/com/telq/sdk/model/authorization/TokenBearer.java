package com.telq.sdk.model.authorization;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * {@link TokenBearer}'s goal is to store the token received from the API endpoint.
 */
@AllArgsConstructor
@Builder
public class TokenBearer {

    @Getter
    @NonNull
    private String token;


}
