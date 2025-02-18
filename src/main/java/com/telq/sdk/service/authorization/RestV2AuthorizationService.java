package com.telq.sdk.service.authorization;

import com.telq.sdk.exceptions.httpExceptions.clientSide.ApiCredentialsException;
import com.telq.sdk.exceptions.httpExceptions.clientSide.AuthorizationServiceException;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.ApiCredentials;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.token.TokenRequestDto;
import com.telq.sdk.service.rest.ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Here we will do all the operations required to handle authorization
 */
public class RestV2AuthorizationService implements AuthorizationService {

    private final ApiCredentials apiCredentials;
    private final ApiConnectorService apiConnectorService;

    private TokenBearer tokenBearer;

    private Instant lastTokenGet;

    public RestV2AuthorizationService(ApiCredentials apiCredentials, ApiConnectorService apiConnectorService) {
        this.apiCredentials = apiCredentials;
        this.apiConnectorService = apiConnectorService;
    }

    /**
     * Here we will make a request for a token, it will save it to this class and return it's value if needed.
     * @return {@link TokenBearer} newly created Token
     */
    @Override
    public TokenBearer requestToken() throws Exception {
        if(!apiCredentials.initialised()) {
            throw new ApiCredentialsException("Api credentials are not correctly initialised, one of the fields is missing");
        }

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(apiCredentials.getAppId())
                .appKey(apiCredentials.getAppKey())
                .build();

        HttpPost post = new HttpPost(TelQUrls.getTokenUrl());
        post.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));

        TokenBearer tokenBearer = apiConnectorService.getToken(post);

        this.lastTokenGet = Instant.now();
        this.tokenBearer = tokenBearer;

        return tokenBearer;
    }

    /**
     * This method will be most used internally by services, to always send Tokens on requests. If the token
     * is more than a day old it will try to fetch it again.
     * @return {@link TokenBearer} current Token
     * @throws AuthorizationServiceException if the token is invalid
     */
    @Override
    public synchronized TokenBearer checkAndGetToken() throws Exception {
        if(tokenBearer != null) {
            if(lastTokenGet.isBefore(Instant.now().minus(24, ChronoUnit.HOURS))) {
                System.out.println("Token more than a day old, trying to retrieve another token");
                try {
                    this.requestToken();;
                } catch (IOException | ApiCredentialsException e) {
                    e.printStackTrace();
                    throw new AuthorizationServiceException("Token invalid");
                }
            }
            return tokenBearer;
        } else
            return this.requestToken();
    }

}
