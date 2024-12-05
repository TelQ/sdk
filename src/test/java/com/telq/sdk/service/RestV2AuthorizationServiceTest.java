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
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

@ExtendWith(
        MockitoExtension.class
)
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

    @BeforeEach
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
        Mockito.lenient().when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);
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
        Mockito.lenient().when(apiConnectorService.getToken(any())).thenThrow(new BadRequest());
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
        Mockito.lenient().when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);
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

        Mockito.lenient().when(apiConnectorService.getToken(any())).thenReturn(tokenBearer);

        TokenBearer modifiedToken = authorizationService.checkAndGetToken();

        assertEquals(tokenBearer, modifiedToken);

        lastTokenGetField.setAccessible(false);
    }

    @Test
    public void testRaceConditionInCheckAndGetToken() throws Exception {
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        // Mock the token to be expired
        TokenBearer expiredToken = TokenBearer.builder().token("EXPIRED_TOKEN").build();
        Mockito.lenient().when(apiConnectorService.getToken(any())).thenReturn(expiredToken);

        // Manually set the lastTokenGet to be more than 24 hours ago
        Field lastTokenGetField = RestV2AuthorizationService.class.getDeclaredField("lastTokenGet");
        lastTokenGetField.setAccessible(true);
        lastTokenGetField.set(authorizationService, Instant.now().minus(25, ChronoUnit.HOURS));

        // Use CountDownLatch to start all threads at the same time
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger tokenRefreshCount = new AtomicInteger(0);

        // Mock apiConnectorService to count token refresh attempts
        Mockito.when(apiConnectorService.getToken(any())).thenAnswer(invocation -> {
            tokenRefreshCount.incrementAndGet();
            // Simulate delay in token retrieval
            Thread.sleep(100);
            return TokenBearer.builder().token("NEW_TOKEN_" + tokenRefreshCount.get()).build();
        });

        // Create tasks for concurrent execution
        Runnable task = () -> {
            try {
                latch.await(); // Wait for the latch to be released
                authorizationService.checkAndGetToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Submit tasks to the executor service
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(task);
        }

        // Release the latch to start all threads
        latch.countDown();

        // Wait for all tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        // Check that just one thread was able to refresh the token
        assertEquals(1, tokenRefreshCount.get());

        lastTokenGetField.setAccessible(false);
    }
}
