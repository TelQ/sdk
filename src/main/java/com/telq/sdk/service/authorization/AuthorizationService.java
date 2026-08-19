package com.telq.sdk.service.authorization;

import com.telq.sdk.model.authorization.ApiCredentials;
import com.telq.sdk.model.authorization.TokenBearer;

public interface AuthorizationService {

    TokenBearer requestToken() throws Exception;

    TokenBearer checkAndGetToken() throws Exception;

    /**
     * Resolves the token a request should be retried with after the server rejected {@code rejectedToken} with a 401.
     *
     * @param rejectedToken the token the server rejected
     * @return the token to retry with, or {@code null} when the caller must not retry
     */
    TokenBearer refreshRejectedToken(TokenBearer rejectedToken) throws Exception;

    /**
     * Signals that a retry performed with a freshly refreshed token was rejected as well, so further refreshes are
     * held back until the backoff window elapses.
     */
    void reportRefreshedTokenRejected();

}
