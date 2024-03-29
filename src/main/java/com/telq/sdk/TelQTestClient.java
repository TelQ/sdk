package com.telq.sdk;

import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.ApiCredentials;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.*;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.authorization.RestV2AuthorizationService;
import com.telq.sdk.service.rest.ApiConnectorService;
import com.telq.sdk.utils.RequestDataValidator;
import com.telq.sdk.service.rest.RestV2ApiConnectorService;
import lombok.NonNull;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TelQTestClient {

    private static TelQTestClient instance = null;

    /*
    Services
     */
    private final AuthorizationService authorizationService;
    private final ApiConnectorService apiConnectorService;

    /*
    Default params
     */
    public static final int DEFAULT_MAX_CALLBACK_RETRIES = 3;
    public static final String DEFAULT_CALLBACK_URL = null;
    public static final String DEFAULT_CALLBACK_TOKEN = null;
    public static final int DEFAULT_TTL = 3600;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    public static final TestIdTextOptions DEFAULT_TEST_ID_TEXT_OPTIONS = TestIdTextOptions.builder().build();

    /**
     * This is the default number of connections to be at disposal for the HttpClient.
     * When the HttpClient makes requests on default, it has one connection that has to be assigned
     * to each method every invocation. Increasing the number allows us to make requests simultaneously.
     */
    private static final int DEFAULT_TOTAL_CONNECTIONS_FOR_CLIENT = 100;


    private TelQTestClient(String appKey, String appId, int totalConnectionsForClient) {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(totalConnectionsForClient);

        this.apiConnectorService = new RestV2ApiConnectorService(HttpClients.custom().setConnectionManager(connManager).build());

        authorizationService = new RestV2AuthorizationService(
                ApiCredentials.builder()
                    .appId(appId)
                    .appKey(appKey)
                    .build(),
                apiConnectorService
                );
    }

    /**
     * Here we will initialise the client for use. For the first call, appKey and id is required
     * @param appKey used for authentication
     * @param appId used for authentication
     * @return Instance of {@link TelQTestClient}
     */
    public static TelQTestClient getInstance(String appKey, String appId) throws Exception {
        if (instance == null) {
            instance = new TelQTestClient(appKey, appId, DEFAULT_TOTAL_CONNECTIONS_FOR_CLIENT);
        }
        return instance;
    }

    /**
     * Here we will initialise the client for use. For the first call, appKey and id is required
     * @param appKey used for authentication
     * @param appId used for authentication
     * @param totalConnectionsForClient is the number of connections to be at disposal for the http client. Default value is 100
     * @return Instance of {@link TelQTestClient}
     */
    public static TelQTestClient getInstance(String appKey, String appId, int totalConnectionsForClient) throws Exception {
        if (instance == null) {
            instance = new TelQTestClient(appKey, appId, totalConnectionsForClient);
        }
        return instance;
    }

    public static TelQTestClient getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("Please initialise with app_key and app_id first.");
        }
        return instance;
    }

    /**
     * Returns the list of all currently available networks.
     * @return List of {@link Network} which represent all currently available networks.
     * @throws Exception
     */
    public List<Network> getNetworks() throws Exception {
        return apiConnectorService.getNetworks(authorizationService, new HttpGet(TelQUrls.getNetworksUrl()));
    }

    /**
     * Makes tests with parameters passed in the TestRequest object
     * @param testRequest test request object with any or none of the optional parameters specified
     * @return List of {@link Test} depending on the number of networks sent this represents the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(TestRequest testRequest) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(testRequest.getNetworks());
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        testRequest.setTestTimeToLive(convertTtlValue(testRequest.getTestTimeToLive(), testRequest.getTimeUnit()));

        HttpPost httpPost = apiConnectorService.buildHttpPostRequest(
                destinationNetworks,
                testRequest.getMaxCallbackRetries(),
                testRequest.getCallbackUrl(),
                testRequest.getTestTimeToLive(),
                testRequest.getCallbackToken(),
                testRequest.getTestIdTextOptions());

        return apiConnectorService.sendTests(
                authorizationService, httpPost);
    }

    /**
     * Make tests with only forwarding networks as a parameter, others are set to default.
     * @param networks list of networks to run tests on.
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        DEFAULT_MAX_CALLBACK_RETRIES,
                        DEFAULT_CALLBACK_URL,
                        DEFAULT_TTL,
                        DEFAULT_CALLBACK_TOKEN,
                        DEFAULT_TEST_ID_TEXT_OPTIONS)
                );
    }

    /**
     * Make tests with only forwarding networks as a parameter, others are set to default.
     * @param networks list of networks to run tests on.
     * @param testIdTextOptions The configuration for the dynamic testIdText feature. Defines the type, case and length of the testIdText.
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull TestIdTextOptions testIdTextOptions) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        DEFAULT_MAX_CALLBACK_RETRIES,
                        DEFAULT_CALLBACK_URL,
                        DEFAULT_TTL,
                        DEFAULT_CALLBACK_TOKEN,
                        testIdTextOptions)
        );
    }

    /**
     * Makes tests with networks and ttl as parameters, others are set as default.
     * @param networks list of networks to run tests on.
     * @param ttl time to live
     * @param timeUnit unit in what the ttl is being sent.
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull int ttl, @NonNull TimeUnit timeUnit) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        ttl = convertTtlValue(ttl, timeUnit);
        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(destinationNetworks,
                        DEFAULT_MAX_CALLBACK_RETRIES,
                        DEFAULT_CALLBACK_URL,
                        ttl,
                        DEFAULT_CALLBACK_TOKEN,
                        DEFAULT_TEST_ID_TEXT_OPTIONS)
                );
    }

    /**
     *  Makes tests with networks, max callback retries, callback url, callback token and ttl.
     * @param networks list of networks to run tests on.
     * @param maxCallbackRetries number of maximum tries for callbacks
     * @param callbackUrl url for callback
     * @param callbackToken token for authorization of callbacks
     * @param ttl time to live
     * @param timeUnit unit in what the ttl is being sent
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull int maxCallbackRetries,
                                       @NonNull String callbackUrl,
                                       @NonNull String callbackToken,
                                       @NonNull int ttl, @NonNull TimeUnit timeUnit) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        ttl = convertTtlValue(ttl, timeUnit);
        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        maxCallbackRetries,
                        callbackUrl,
                        ttl,
                        callbackToken,
                        DEFAULT_TEST_ID_TEXT_OPTIONS));
    }

    /**
     *  Makes tests with networks, max callback retries, callback url, callback token and ttl.
     * @param networks list of networks to run tests on.
     * @param maxCallbackRetries number of maximum tries for callbacks
     * @param callbackUrl url for callback
     * @param callbackToken token for authorization of callbacks
     * @param ttl time to live
     * @param timeUnit unit in what the ttl is being sent
     * @param testIdTextOptions The configuration for the dynamic testIdText feature. Defines the type, case and length of the testIdText.
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull int maxCallbackRetries,
                                       @NonNull String callbackUrl,
                                       @NonNull String callbackToken,
                                       @NonNull int ttl, @NonNull TimeUnit timeUnit,
                                       @NonNull TestIdTextOptions testIdTextOptions) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        ttl = convertTtlValue(ttl, timeUnit);
        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        maxCallbackRetries,
                        callbackUrl,
                        ttl,
                        callbackToken,
                        testIdTextOptions));
    }

    /**
     * Makes tests with networks, max callback retries, callback url, callback token. Takes default ttl
     * @param networks list of networks to run tests on.
     * @param maxCallbackRetries number of maximum tries for callbacks
     * @param callbackUrl url for callback
     * @param callbackToken token for authorization of callbacks
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull int maxCallbackRetries,
                                       @NonNull String callbackUrl,
                                       @NonNull String callbackToken) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        maxCallbackRetries,
                        callbackUrl,
                        DEFAULT_TTL,
                        callbackToken,
                        DEFAULT_TEST_ID_TEXT_OPTIONS));
    }

    /**
     * Makes tests with networks, callback url, callback token. Takes default ttl and max callback rertries
     * @param networks list of networks to run tests on.
     * @param callbackUrl url for callback
     * @param callbackToken token for authorization of callbacks
     * @return List of {@link Test} depending on the number of networks sent this represent the test initiated.
     * @throws Exception
     */
    public List<Test> initiateNewTests(@NonNull List<Network> networks,
                                       @NonNull String callbackUrl,
                                       @NonNull String callbackToken) throws Exception {
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        if(!RequestDataValidator.validateNetworks(destinationNetworks))
            throw new Exception("Incorrect data passed in networks.");

        return apiConnectorService.sendTests(
                authorizationService,
                apiConnectorService.buildHttpPostRequest(
                        destinationNetworks,
                        DEFAULT_MAX_CALLBACK_RETRIES,
                        callbackUrl,
                        DEFAULT_TTL,
                        callbackToken,
                        DEFAULT_TEST_ID_TEXT_OPTIONS));
    }


    /**
     * Query for the test result with the given id
     * @param testId with which the query is done.
     * @return {@link Result} of the test
     * @throws Exception
     */
    public Result getTestResult(Long testId) throws Exception {
        if(testId <= 0)
            throw new Exception("Invalid id passed");

        HttpGet request = new HttpGet(TelQUrls.getResultsUrl() + "/" + testId);

        return apiConnectorService.getTestResult(authorizationService, request);
    }

    /**
     * Here our goal is on depending what our user gave us to convert it to seconds, since we mostly use seconds
     * but we want to allow them to have it as minutes or hours
     * @param ttl given
     * @param timeUnit in what form it has been given
     * @return {@link Integer} in converted value
     */
    private int convertTtlValue(int ttl, TimeUnit timeUnit) {
        switch (timeUnit) {
            case HOURS:
                return ttl * 60 * 60;
            case MINUTES:
                return ttl * 60;
            case SECONDS:
                return ttl;
            default: return -1;
        }
    }

    /**
     * Since the response when requesting the networks and the networks we send to initiate a test are different
     * we have to map one to another, we do it on our side, so the SDK user doesn't have to.
     * @param networks received from get networks response
     * @return List of {@link DestinationNetwork} that have been mapped.
     */
    private List<DestinationNetwork> convertToDestinationNetwork(List<Network> networks) {
        List<DestinationNetwork> destinationNetworks = new ArrayList<>();

        networks.forEach(network ->
            destinationNetworks.add(DestinationNetwork.builder()
                    .mcc(network.getMcc())
                    .mnc(network.getMnc())
                    .portedFromMnc(network.getPortedFromMnc())
                    .build())
        );

        return destinationNetworks;
    }

}
