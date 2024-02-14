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
    public LntApiCreateTestResponseDto createTests(LntApiTestRequestDto testRequestDto) {
        Type type = new TypeToken<LntApiCreateTestResponseDto>() {
        }.getType();
        return restClient.httpPost(lntTestsUrl, testRequestDto, type);
    }

    @Override
    public Page<LntApiTestResultDto> getTestPage(PageConf pageConf, Instant from, Instant to) {
        Map<String, String> queryParams = new HashMap<>();
        if (from != null) queryParams.put("from", from.toString());
        if (to != null) queryParams.put("to", to.toString());
        if (pageConf != null) {
            if (pageConf.getPage() != null) queryParams.put("page", pageConf.getPage().toString());
            if (pageConf.getSize() != null) queryParams.put("size", pageConf.getSize().toString());
            if (pageConf.getOrder() != null) queryParams.put("order", String.valueOf(pageConf.getOrder()));
        }
        Type type = new TypeToken<Page<LntApiTestResultDto>>() {}.getType();
        return restClient.httpGet(lntTestsUrl, type, queryParams);
    }

    @Override
    public LntApiTestResultDto getTestById(Long testId) {
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
    public LntApiSessionDto getSessionById(Long sessionId) {
        return null;
    }

    @Override
    public void deleteSessionById(Long sessionId) {

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
