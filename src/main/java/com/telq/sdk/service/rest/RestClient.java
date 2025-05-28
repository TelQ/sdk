package com.telq.sdk.service.rest;

import com.google.gson.Gson;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.utils.JsonMapper;
import com.telq.sdk.utils.VersionReader;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.lang.reflect.Type;
import java.util.Map;

public class RestClient {
    private final CloseableHttpClient httpClient;
    private final AuthorizationService authorizationService;
    private final Gson mapper = JsonMapper.getInstance().getMapper();

    public RestClient(CloseableHttpClient httpClient, AuthorizationService authorizationService) {
        this.httpClient = httpClient;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    public <T> T httpGet(String url, Type responseType, Map<String, String> queryParams) {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        HttpGet request = new HttpGet(uriBuilder.build());
        request.addHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        try (CloseableHttpResponse response = executeWithTokenRetry(request)) {
            String json = EntityUtils.toString(response.getEntity());
            return mapper.fromJson(json, responseType);
        }
    }

    public <T, R> R httpPost(String url, T body, Type responseType) {
        HttpPost request = new HttpPost(url);
        request.addHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    public <T, R> R httpPut(String url, T body, Type responseType) {
        HttpPut request = new HttpPut(url);
        request.addHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    private <T, R> R requestWithBody(T body, Type responseType, HttpUriRequestBase request) {
        request.setEntity(getRequestEntity(body));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        try (CloseableHttpResponse response = executeWithTokenRetry(request)) {
            String responseJson = EntityUtils.toString(response.getEntity());
            System.out.println(responseJson);
            return mapper.fromJson(responseJson, responseType);
        }
    }

    private <T> StringEntity getRequestEntity(T body){
        String json = mapper.toJson(body);
        return new StringEntity(json, ContentType.create("text/plain", "UTF-8"));
    }

    @SneakyThrows
    private void useToken(HttpUriRequestBase request, Boolean forceReset) {
        if (forceReset) authorizationService.requestToken();
        TokenBearer tokenBearer = authorizationService.checkAndGetToken();
        request.setHeader("Authorization", "Bearer " + tokenBearer.getToken());
    }

    @SneakyThrows
    private CloseableHttpResponse executeWithTokenRetry(HttpUriRequestBase request) {
        useToken(request, false);
        CloseableHttpResponse response = httpClient.execute(request);
        if (response.getCode() == 401) {
            response.close();
            useToken(request, true);
            response = httpClient.execute(request);
        }
        return response;
    }

    @SneakyThrows
    public <T> void httpPostNoResponse(String url, T body) {
        HttpPost request = new HttpPost(url);
        request.setEntity(getRequestEntity(body));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.addHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        try (CloseableHttpResponse response = executeWithTokenRetry(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }

    @SneakyThrows
    public <T> void httpPutNoResponse(String url, T body) {
        HttpPut request = new HttpPut(url);
        request.setEntity(getRequestEntity(body));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        try (CloseableHttpResponse response = executeWithTokenRetry(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }

    @SneakyThrows
    public void httpDelete(String url) {
        HttpDelete request = new HttpDelete(url);
        request.addHeader("User-Agent", "java-sdk/" + VersionReader.getVersion());
        try (CloseableHttpResponse response = executeWithTokenRetry(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }
}

