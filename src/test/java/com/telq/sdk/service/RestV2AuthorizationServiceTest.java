package com.telq.sdk.service;

import com.telq.sdk.BaseTest;
import com.telq.sdk.exceptions.httpExceptions.clientSide.BadRequest;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.ApiCredentials;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.token.TokenRequestDto;
import com.telq.sdk.service.authorization.RestV2AuthorizationService;
import com.telq.sdk.service.rest.ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RestV2AuthorizationServiceTest extends BaseTest {

    @Mock
    private ApiConnectorService apiConnectorService;

    private final ApiCredentials correctApiCredentials = ApiCredentials.builder().appId(appId).appKey(appKey).build();
    private final ApiCredentials incorrectApiCredentials = ApiCredentials.builder().appId("incorrectId").appKey("incorrectKey").build();

    private final TokenBearer tokenBearer = TokenBearer.builder().token(token).build();

    @Mock
    private HttpPost tokenPostCorrect;

    @Mock
    private HttpPost tokenPostIncorrect;

    @Before
    public void setup() throws Exception {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        HttpPost post = new HttpPost(TelQUrls.getTokenUrl());
        post.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));

    }

    @Test
    public void requestToken_correctCredentials_pass() throws Exception {
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer tokenBearer = authorizationService.requestToken();

        assertEquals(this.tokenBearer, tokenBearer);
    }

    @Test
    public void requestToken_incorrectCredentials_exceptionThrown() throws Exception {
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        when(apiConnectorService.getToken(any())).thenThrow(new BadRequest());
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        try {
            authorizationService.requestToken();
        } catch (Exception exception) {
            assertTrue(exception instanceof BadRequest);
        }
    }

    @Test
    public void checkAndGetToken_fetchAgain_pass() throws Exception {
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer tokenBearer = authorizationService.requestToken();

        assertEquals(this.tokenBearer, tokenBearer);

        Field lastTokenGetField = RestV2AuthorizationService.class.getDeclaredField("lastTokenGet");
        lastTokenGetField.setAccessible(true);
        lastTokenGetField.set(authorizationService, Instant.now().minus(30, ChronoUnit.HOURS));

        tokenBearer = TokenBearer.builder().token("TEST_TOKEN").build();

        when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);

        TokenBearer modifiedToken = authorizationService.checkAndGetToken();

        assertEquals(tokenBearer, modifiedToken);

        lastTokenGetField.setAccessible(false);
    }

}
