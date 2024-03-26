package com.telq.sdk.clients;

import com.telq.sdk.model.v3.lnt.*;

import java.time.Instant;

public interface LiveNumberTestingClient {
    LntApiCreateTestResponseDto createTests(LntApiTestRequestDto testRequestDto);
    Page<LntApiTestResultDto> getTestPage(PageConf pageConf, Instant from, Instant to);
    LntApiTestResultDto getTestById(Long testId);

    LntApiSessionCreationResponseDto createSession(LntApiCreateSessionDto sessionDto);
    void updateSession(LntApiCreateSessionDto sessionDto);
    Page<LntApiSessionDto> getSessions(PageConf pageConf);
    LntApiSessionDto getSessionById(Long sessionId);
    void deleteSessionById(Long sessionId);

    LntApiSupplierCreationResponseDto createSupplier(LntApiCreateSupplierDto supplierDto);
    void updateSupplier(LntApiCreateSupplierDto supplierDto);
    Page<LntApiSupplierDto> getSuppliers(PageConf pageConf);
    LntApiSupplierDto getSupplierById(Long supplierId);
    void deleteSupplierById(Long supplierId);

    void assignSuppliersToSession(LntApiAssignSuppliersDto assignSuppliersDto);

    Page<LntApiSessionsSuppliersStatusResponse> getSessionSuppliers(PageConf pageConf);

}
