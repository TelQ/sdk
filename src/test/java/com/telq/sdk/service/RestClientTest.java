package com.telq.sdk.service;

import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestClientTest {

    @Mock
    private CloseableHttpClient mockHttpClient;

    @Mock
    private AuthorizationService mockAuthorizationService;

    @Mock
    private CloseableHttpResponse mockResponse;

    private RestClient restClient;

    private final TokenBearer mockTokenBearer = new TokenBearer("mockToken");

    @BeforeEach
    void setUp() throws Exception {
        restClient = new RestClient(mockHttpClient, mockAuthorizationService);
        when(mockAuthorizationService.checkAndGetToken()).thenReturn(mockTokenBearer);
        when(mockHttpClient.execute(any(HttpUriRequestBase.class))).thenReturn(mockResponse);
    }

    @Test
    void testHttpGet_success() throws Exception {
        String jsonResponse = "{\"result\":\"success\"}";
        when(mockResponse.getEntity()).thenReturn(new StringEntity(jsonResponse, StandardCharsets.UTF_8));
        when(mockResponse.getCode()).thenReturn(200);

        TestResponse response = restClient.httpGet("http://example.com", TestResponse.class, Collections.emptyMap());

        assertNotNull(response);
        assertEquals("success", response.getResult());
        verify(mockAuthorizationService, times(1)).checkAndGetToken();
    }

    @Test
    void testHttpPost_success() throws Exception {
        String jsonResponse = "{\"result\":\"created\"}";
        when(mockResponse.getEntity()).thenReturn(new StringEntity(jsonResponse, StandardCharsets.UTF_8));
        when(mockResponse.getCode()).thenReturn(201);

        TestRequest request = new TestRequest("test");
        TestResponse response = restClient.httpPost("http://example.com", request, TestResponse.class);

        assertNotNull(response);
        assertEquals("created", response.getResult());
        verify(mockAuthorizationService, times(1)).checkAndGetToken();
    }

    @Test
    void testHttpDelete_success() throws Exception {
        when(mockResponse.getCode()).thenReturn(200);

        assertDoesNotThrow(() -> restClient.httpDelete("http://example.com"));
        verify(mockAuthorizationService, times(1)).checkAndGetToken();
    }

    @Test
    void testExecuteWithTokenRetry_retriesOnUnauthorized() throws Exception {
        when(mockResponse.getCode()).thenReturn(401).thenReturn(200);

        restClient.httpDelete("http://example.com");

        verify(mockAuthorizationService, times(1)).requestToken();
        verify(mockHttpClient, times(2)).execute(any(HttpUriRequestBase.class));
    }

    @Getter
    static class TestResponse {
        private String result;
    }

    @Getter
    @AllArgsConstructor
    static class TestRequest {
        private String name;
    }
}
