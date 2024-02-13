package com.telq.sdk.service.rest;

import com.google.gson.Gson;
import com.telq.sdk.utils.JsonMapper;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.lang.reflect.Type;
import java.util.Map;

public class RestClient {
    private final CloseableHttpClient httpClient;
    private final Gson mapper = JsonMapper.getInstance().getMapper();

    public RestClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
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
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String json = EntityUtils.toString(response.getEntity());
            return  mapper.fromJson(json, responseType);
        }
    }

    public <T, R> R httpPost(String url, T body, Type responseType) {
        HttpPost request = new HttpPost(url);
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    public <T, R> R httpPut(String url, T body,  Type responseType)  {
        HttpPut request = new HttpPut(url);
        return requestWithBody(body, responseType, request);
    }

    @SneakyThrows
    private <T, R> R requestWithBody(T body,  Type responseType, HttpUriRequestBase request) {
        String json = mapper.toJson(body);
        request.setEntity(new StringEntity(json));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseJson = EntityUtils.toString(response.getEntity());
            return mapper.fromJson(responseJson, responseType);
        }
    }

    @SneakyThrows
    public void httpDelete(String url) {
        HttpDelete request = new HttpDelete(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
        }
    }
}

