package com.telq.sdk.service.rest;

import com.google.gson.Gson;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.utils.JsonMapper;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
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
        useToken(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String json = EntityUtils.toString(response.getEntity());
            return mapper.fromJson(json, responseType);
        }
    }

    public <T, R> R httpPost(String url, T body, Type responseType) {
        HttpPost request = new HttpPost(url);
        useToken(request);
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    public <T, R> R httpPut(String url, T body, Type responseType) {
        HttpPut request = new HttpPut(url);
        useToken(request);
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    private <T, R> R requestWithBody(T body, Type responseType, HttpUriRequestBase request) {
        String json = mapper.toJson(body);
        request.setEntity(new StringEntity(json));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        useToken(request);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseJson = EntityUtils.toString(response.getEntity());
            System.out.println(responseJson);
            return mapper.fromJson(responseJson, responseType);
        }
    }

    @SneakyThrows
    private void useToken(HttpUriRequestBase request) {
        TokenBearer tokenBearer = authorizationService.checkAndGetToken();
        request.setHeader("Authorization", "Bearer " + tokenBearer.getToken());
    }

    @SneakyThrows
    public <T> void httpPostNoResponse(String url, T body) {
        HttpPost request = new HttpPost(url);
        String json = mapper.toJson(body);
        request.setEntity(new StringEntity(json));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        useToken(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }

    @SneakyThrows
    public <T> void httpPutNoResponse(String url, T body) {
        HttpPut request = new HttpPut(url);
        String json = mapper.toJson(body);
        request.setEntity(new StringEntity(json));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        useToken(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }

    @SneakyThrows
    public void httpDelete(String url) {
        HttpDelete request = new HttpDelete(url);
        useToken(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 200)
                throw new RuntimeException(response.getCode() + " " + response.getReasonPhrase());
        }
    }
}

