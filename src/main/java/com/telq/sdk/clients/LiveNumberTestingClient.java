package com.telq.sdk.clients;

import com.telq.sdk.model.v3.lnt.*;

import java.time.Instant;
import java.util.List;

public interface LiveNumberTestingClient {
    LntApiCreateTestResponseDto createTests(LntApiTestRequestDto testRequestDto);
    Page<LntApiTestResultDto> getTestPage(PageConf pageConf, Instant from, Instant to);
    LntApiTestResultDto getTestById(Long testId);

    LntApiSessionDto createSession(LntApiCreateSessionDto sessionDto);
    LntApiSessionDto updateSession(LntApiCreateSessionDto sessionDto);
    Page<LntApiSessionDto> getSessions(PageConf pageConf);
    LntApiSessionDto getSessionById(Long sessionId);
    void deleteSessionById(Long sessionId);

    LntApiSupplierDto createSupplier(LntApiCreateSupplierDto supplierDto);
    LntApiSupplierDto updateSupplier(LntApiCreateSupplierDto supplierDto);
    Page<LntApiSupplierDto> getSuppliers(PageConf pageConf);
    LntApiSupplierDto getSupplierById(Long supplierId);
    List<LntApiSupplierDto> getSuppliersBySessionId(Long sessionId);
    void deleteSupplierById(Long supplierId);

    void assignSuppliersToSession(LntApiAssignSuppliersDto assignSuppliersDto);

    Page<LntApiSessionSupplierDto> getSessionSuppliers(PageConf pageConf);

}
