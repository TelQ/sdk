package com.telq.sdk.service.rest;

import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.tests.Test;
import com.telq.sdk.service.authorization.AuthorizationService;
import lombok.NonNull;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.List;

public interface ApiConnectorService {

    TokenBearer getToken(@NonNull HttpRequestBase request) throws Exception;

    List<Network> getNetworks(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request) throws Exception;

    List<Test> sendTests(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request) throws Exception;

    Result getTestResult(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request) throws Exception;
}
