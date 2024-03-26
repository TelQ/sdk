package com.telq.sdk.clients;

import com.google.gson.reflect.TypeToken;
import com.telq.sdk.model.v3.lnt.LntApiAssignSuppliersDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateSessionDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateSupplierDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateTestResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionCreationResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionsSuppliersStatusResponse;
import com.telq.sdk.model.v3.lnt.LntApiSupplierCreationResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSupplierDto;
import com.telq.sdk.model.v3.lnt.LntApiTestRequestDto;
import com.telq.sdk.model.v3.lnt.LntApiTestResultDto;
import com.telq.sdk.model.v3.lnt.Page;
import com.telq.sdk.model.v3.lnt.PageConf;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.telq.sdk.model.TelQUrls.baseUrlV3;
import static com.telq.sdk.model.TelQUrls.lntSessionsUrl;
import static com.telq.sdk.model.TelQUrls.lntSuppliersUrl;
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
        Type type = new TypeToken<Page<LntApiTestResultDto>>() {
        }.getType();
        return restClient.httpGet(lntTestsUrl, type, queryParams);
    }

    @Override
    public LntApiTestResultDto getTestById(Long testId) {
        return restClient.httpGet(lntTestsUrl + "/" + testId, LntApiTestResultDto.class, null);
    }

    @Override
    public LntApiSessionCreationResponseDto createSession(LntApiCreateSessionDto sessionDto) {
        if (sessionDto.getSmppSessionId() != null)
            throw new IllegalArgumentException("smppSessionId has to be absent for sessions creation");
        return restClient.httpPost(lntSessionsUrl, sessionDto, LntApiSessionCreationResponseDto.class);
    }

    @Override
    public void updateSession(LntApiCreateSessionDto sessionDto) {
        if (sessionDto.getSmppSessionId() == null)
            throw new IllegalArgumentException("smppSessionId has to be specified for sessions update");
        restClient.httpPutNoResponse(lntSessionsUrl, sessionDto);
    }

    @Override
    public Page<LntApiSessionDto> getSessions(PageConf pageConf) {
        Map<String, String> queryParams = new HashMap<>();
        if (pageConf != null) {
            if (pageConf.getPage() != null) queryParams.put("page", pageConf.getPage().toString());
            if (pageConf.getSize() != null) queryParams.put("size", pageConf.getSize().toString());
            if (pageConf.getOrder() != null) queryParams.put("order", String.valueOf(pageConf.getOrder()));
        } else {
            queryParams.put("page", "0");
            queryParams.put("size", "20");
        }
        Type type = new TypeToken<Page<LntApiSessionDto>>() {
        }.getType();
        return restClient.httpGet(lntSessionsUrl, type, queryParams);
    }

    @Override
    public LntApiSessionDto getSessionById(Long sessionId) {
        return restClient.httpGet(lntSessionsUrl + "/" + sessionId, LntApiSessionDto.class, null);
    }

    @Override
    public void deleteSessionById(Long sessionId) {
        restClient.httpDelete(lntSessionsUrl + "/" + sessionId);
    }

    @Override
    public LntApiSupplierCreationResponseDto createSupplier(LntApiCreateSupplierDto supplierDto) {
        if (supplierDto.getSmppSessionId() == null)
            throw new IllegalArgumentException("smpp session id has to be specified for supplier");
        return restClient.httpPost(lntSuppliersUrl, supplierDto, LntApiSupplierCreationResponseDto.class);
    }

    @Override
    public void updateSupplier(LntApiCreateSupplierDto supplierDto) {
        restClient.httpPutNoResponse(lntSuppliersUrl, supplierDto);
    }

    @Override
    public Page<LntApiSupplierDto> getSuppliers(PageConf pageConf) {
        Map<String, String> queryParams = new HashMap<>();
        if (pageConf != null) {
            if (pageConf.getPage() != null) queryParams.put("page", pageConf.getPage().toString());
            if (pageConf.getSize() != null) queryParams.put("size", pageConf.getSize().toString());
            if (pageConf.getOrder() != null) queryParams.put("order", String.valueOf(pageConf.getOrder()));
        } else {
            queryParams.put("page", "0");
            queryParams.put("size", "20");
        }
        Type type = new TypeToken<Page<LntApiSupplierDto>>() {
        }.getType();
        return restClient.httpGet(lntSuppliersUrl, type, queryParams);
    }

    @Override
    public LntApiSupplierDto getSupplierById(Long supplierId) {
        return restClient.httpGet(lntSuppliersUrl + "/" + supplierId, LntApiSupplierDto.class, null);
    }

    @Override
    public void deleteSupplierById(Long supplierId) {
        restClient.httpDelete(lntSuppliersUrl + "/" + supplierId);
    }

    @Override
    public void assignSuppliersToSession(LntApiAssignSuppliersDto assignSuppliersDto) {
        restClient.httpPostNoResponse(lntSuppliersUrl + "/assign", assignSuppliersDto);
    }

    @Override
    public Page<LntApiSessionsSuppliersStatusResponse> getSessionSuppliers(PageConf pageConf) {
        Map<String, String> queryParams = new HashMap<>();
        if (pageConf != null) {
            if (pageConf.getPage() != null) queryParams.put("page", pageConf.getPage().toString());
            if (pageConf.getSize() != null) queryParams.put("size", pageConf.getSize().toString());
            if (pageConf.getOrder() != null) queryParams.put("order", String.valueOf(pageConf.getOrder()));
        } else {
            queryParams.put("page", "0");
            queryParams.put("size", "20");
        }
        Type type = new TypeToken<Page<LntApiSessionsSuppliersStatusResponse>>() {
        }.getType();
        return restClient.httpGet(baseUrlV3 + "/sessions-suppliers", type, queryParams);
    }
}
