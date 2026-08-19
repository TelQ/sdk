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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    public void checkAndGetToken_concurrentFirstUseRequestsOneToken() throws Exception {
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );
        AtomicInteger tokenRequests = new AtomicInteger();
        CountDownLatch callersReady = new CountDownLatch(8);
        CountDownLatch start = new CountDownLatch(1);

        Mockito.when(apiConnectorService.getToken(any())).thenAnswer(invocation -> {
            tokenRequests.incrementAndGet();
            Thread.sleep(100);
            return tokenBearer;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<TokenBearer>> futures = new ArrayList<>();
        try {
            for(int i = 0; i < 8; i++) {
                futures.add(executorService.submit(() -> {
                    callersReady.countDown();
                    assertTrue(start.await(5, TimeUnit.SECONDS));
                    return authorizationService.checkAndGetToken();
                }));
            }

            assertTrue(callersReady.await(5, TimeUnit.SECONDS));
            start.countDown();

            for(Future<TokenBearer> future : futures) {
                assertEquals(tokenBearer, future.get(5, TimeUnit.SECONDS));
            }
        } finally {
            executorService.shutdownNow();
        }

        assertEquals(1, tokenRequests.get());
        verify(apiConnectorService, times(1)).getToken(any());
    }

    @Test
    public void refreshRejectedToken_stillCurrentToken_requestsNewToken() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();

        assertEquals(this.tokenBearer, rejectedToken);
        assertEquals(refreshedToken, authorizationService.refreshRejectedToken(rejectedToken));
        verify(apiConnectorService, times(2)).getToken(any());
    }

    @Test
    public void refreshRejectedToken_alreadyRefreshedByAnotherCaller_reusesCurrentToken() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();
        authorizationService.requestToken();

        assertEquals(refreshedToken, authorizationService.refreshRejectedToken(rejectedToken));
        verify(apiConnectorService, times(2)).getToken(any());
    }

    @Test
    public void refreshRejectedToken_rejectedRefreshWithinBackoff_returnsNull() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();
        TokenBearer firstRefresh = authorizationService.refreshRejectedToken(rejectedToken);
        authorizationService.reportRefreshedTokenRejected();

        assertEquals(refreshedToken, firstRefresh);
        assertNull(authorizationService.refreshRejectedToken(refreshedToken));
        assertNull(authorizationService.refreshRejectedToken(refreshedToken));
        verify(apiConnectorService, times(2)).getToken(any());
    }

    @Test
    public void refreshRejectedToken_backoffElapsed_refreshesAgain() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        TokenBearer laterToken = TokenBearer.builder().token("later-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken, laterToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();
        authorizationService.refreshRejectedToken(rejectedToken);
        authorizationService.reportRefreshedTokenRejected();

        assertNull(authorizationService.refreshRejectedToken(refreshedToken));

        setLastRejectedRefresh(authorizationService, Instant.now().minus(610, ChronoUnit.SECONDS));

        assertEquals(laterToken, authorizationService.refreshRejectedToken(refreshedToken));
        verify(apiConnectorService, times(3)).getToken(any());
    }

    @Test
    public void refreshRejectedToken_backoffHoldsForLessThanTenMinutes() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();
        authorizationService.refreshRejectedToken(rejectedToken);
        authorizationService.reportRefreshedTokenRejected();
        setLastRejectedRefresh(authorizationService, Instant.now().minus(590, ChronoUnit.SECONDS));

        assertNull(authorizationService.refreshRejectedToken(refreshedToken));
        verify(apiConnectorService, times(2)).getToken(any());
    }

    @Test
    public void requestToken_successfulRefresh_clearsBackoff() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        TokenBearer laterToken = TokenBearer.builder().token("later-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        Mockito.when(apiConnectorService.getToken(any())).thenReturn(tokenBearer, refreshedToken, laterToken);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();
        authorizationService.refreshRejectedToken(rejectedToken);
        authorizationService.reportRefreshedTokenRejected();
        authorizationService.requestToken();

        assertEquals(laterToken, authorizationService.checkAndGetToken());
        assertNull(getLastRejectedRefresh(authorizationService));
    }

    @Test
    public void refreshRejectedToken_concurrentCallersRequestOneToken() throws Exception {
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        ApiConnectorService apiConnectorService = mock(ApiConnectorService.class);
        RestV2AuthorizationService authorizationService = new RestV2AuthorizationService(
                correctApiCredentials,
                apiConnectorService
        );
        AtomicInteger tokenRequests = new AtomicInteger();
        CountDownLatch callersReady = new CountDownLatch(8);
        CountDownLatch start = new CountDownLatch(1);

        Mockito.when(apiConnectorService.getToken(any())).thenAnswer(invocation ->
                tokenRequests.incrementAndGet() == 1 ? tokenBearer : refreshedToken
        );

        TokenBearer rejectedToken = authorizationService.checkAndGetToken();

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<TokenBearer>> futures = new ArrayList<>();
        try {
            for(int i = 0; i < 8; i++) {
                futures.add(executorService.submit(() -> {
                    callersReady.countDown();
                    assertTrue(start.await(5, TimeUnit.SECONDS));
                    return authorizationService.refreshRejectedToken(rejectedToken);
                }));
            }

            assertTrue(callersReady.await(5, TimeUnit.SECONDS));
            start.countDown();

            for(Future<TokenBearer> future : futures) {
                assertEquals(refreshedToken, future.get(5, TimeUnit.SECONDS));
            }
        } finally {
            executorService.shutdownNow();
        }

        assertEquals(2, tokenRequests.get());
        verify(apiConnectorService, times(2)).getToken(any());
    }

    @Test
    public void backoffWindow_staysUnderTokenEndpointRateLimit() throws Exception {
        Field backoffField = RestV2AuthorizationService.class.getDeclaredField("REJECTED_REFRESH_BACKOFF");
        backoffField.setAccessible(true);
        Duration backoff = (Duration) backoffField.get(null);

        long refreshesPerHour = Duration.ofHours(1).toMillis() / backoff.toMillis();

        assertTrue(
                refreshesPerHour <= 10,
                "the token endpoint allows 10 requests per hour per appId, but the backoff window permits "
                        + refreshesPerHour
        );
    }

    private void setLastRejectedRefresh(RestV2AuthorizationService authorizationService, Instant value) throws Exception {
        Field lastRejectedRefreshField = RestV2AuthorizationService.class.getDeclaredField("lastRejectedRefresh");
        lastRejectedRefreshField.setAccessible(true);
        lastRejectedRefreshField.set(authorizationService, value);
    }

    private Instant getLastRejectedRefresh(RestV2AuthorizationService authorizationService) throws Exception {
        Field lastRejectedRefreshField = RestV2AuthorizationService.class.getDeclaredField("lastRejectedRefresh");
        lastRejectedRefreshField.setAccessible(true);
        return (Instant) lastRejectedRefreshField.get(authorizationService);
    }

}
