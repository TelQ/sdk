package com.telq.sdk.clients;

import com.google.gson.reflect.TypeToken;
import com.telq.sdk.model.v3.lnt.*;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.telq.sdk.model.TelQUrls.lntTestsUrl;

public class LntClient implements LiveNumberTestingClient {
    private final RestClient restClient;

    public LntClient(CloseableHttpClient httpClient, AuthorizationService authorizationService) {
        this.restClient = new RestClient(httpClient, authorizationService);
    }

    @Override
    public List<LntApiCreateTestResponseDto> createTests(LntApiTestRequestDto testRequestDto) {
        Type type = new TypeToken<List<LntApiCreateTestResponseDto>>() {}.getType();
        return restClient.httpPost(lntTestsUrl, testRequestDto,type);
    }

    @Override
    public Page<LntApiTestResultDto> getTestResults(PageConf pageConf, Instant from, Instant to) {
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("from", from.toString());
        queryParams.put("to", to.toString());
        queryParams.put("page", pageConf.getPage().toString());
        queryParams.put("size", pageConf.getSize().toString());
        queryParams.put("order", String.valueOf(pageConf.getOrder()));
        Type type = new TypeToken<Page<LntApiTestResultDto>>() {}.getType();
        return restClient.httpGet(lntTestsUrl, type, queryParams);
    }

    @Override
    public LntApiTestResultDto getTestResultById(String testId) {
        return restClient.httpGet(lntTestsUrl + "/" + testId, LntApiTestResultDto.class, null);
    }

    @Override
    public LntApiSessionDto createSession(LntApiCreateSessionDto sessionDto) {
        return null;
    }

    @Override
    public LntApiSessionDto updateSession(LntApiCreateSessionDto sessionDto) {
        return null;
    }

    @Override
    public Page<LntApiSessionDto> getSessions(PageConf pageConf) {
        return null;
    }

    @Override
    public LntApiSessionDto getSessionById(String sessionId) {
        return null;
    }

    @Override
    public void deleteSessionById(String sessionId) {

    }

    @Override
    public LntApiSupplierDto createSupplier(LntApiCreateSupplierDto supplierDto) {
        return null;
    }

    @Override
    public LntApiSupplierDto updateSupplier(LntApiCreateSupplierDto supplierDto) {
        return null;
    }

    @Override
    public Page<LntApiSupplierDto> getSuppliers(PageConf pageConf) {
        return null;
    }

    @Override
    public LntApiSupplierDto getSupplierById(Long supplierId) {
        return null;
    }

    @Override
    public List<LntApiSupplierDto> getSuppliersBySessionId(Long sessionId) {
        return null;
    }

    @Override
    public void deleteSupplierById(Long supplierId) {

    }

    @Override
    public void assignSuppliersToSession(LntApiAssignSuppliersDto assignSuppliersDto) {

    }

    @Override
    public Page<LntApiSessionSupplierDto> getSessionSuppliers(PageConf pageConf) {
        return null;
    }
}
