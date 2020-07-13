package com.telq.sdk.service.authorization;

import com.telq.sdk.model.authorization.ApiCredentials;
import com.telq.sdk.model.authorization.TokenBearer;

public interface AuthorizationService {

    TokenBearer requestToken() throws Exception;

    TokenBearer checkAndGetToken() throws Exception;

}
