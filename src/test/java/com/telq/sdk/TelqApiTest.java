package com.telq.sdk;

import com.telq.sdk.model.tests.TestIdTextCase;
import com.telq.sdk.model.tests.TestIdTextType;
import com.telq.sdk.model.v3.lnt.LntApiAssignSuppliersDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateOrUpdateSessionDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateOrUpdateSupplierDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateTestDto;
import com.telq.sdk.model.v3.lnt.LntApiCreateTestResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionCreationResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionDto;
import com.telq.sdk.model.v3.lnt.LntApiSessionsSuppliersStatusResponse;
import com.telq.sdk.model.v3.lnt.LntApiSupplierCreationResponseDto;
import com.telq.sdk.model.v3.lnt.LntApiSupplierDto;
import com.telq.sdk.model.v3.lnt.LntApiTestRequestDto;
import com.telq.sdk.model.v3.lnt.LntApiTestResultDto;
import com.telq.sdk.model.v3.lnt.LntApiTlvDto;
import com.telq.sdk.model.v3.lnt.LntApiUdhDto;
import com.telq.sdk.model.v3.lnt.Page;
import com.telq.sdk.model.v3.lnt.PageConf;
import com.telq.sdk.model.v3.lnt.RouteAttribute;
import com.telq.sdk.model.v3.mt.MtApiTestResultDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Disabled
class TelqApiTest {
    String testApiKey = "your-api-key-here";
    String appId = "your-app-id-here";

    @Test
    void liveTestingApiCalls_allSucceed() {
        TelqApi telqApi = new TelqApi(testApiKey, appId);

        Page<LntApiTestResultDto> tests = telqApi.lnt.getTestPage(null, null, null);
        System.out.println(tests);

        LntApiTestResultDto test = telqApi.lnt.getTestById(1155470L);
        System.out.println(test);

        LntApiTestRequestDto testRequestDto = LntApiTestRequestDto.builder()
                .maxCallbackRetries(3)
                .dataCoding("01")
                .commentText("my comment")
                .priorityFlag(Byte.valueOf("1"))
                .testTimeToLiveInSeconds(360)
                .sourceNpi("02")
                .sourceTon("01")
                .smppValidityPeriod(360)
                .resultsCallbackUrl("https://example.com/callback")
                .sendTextAsMessagePayloadTlv(false)
                .scheduledDeliveryTime("2209131247000000")
                .replaceIfPresentFlag(false)
                .udh(Collections.singletonList(LntApiUdhDto.builder()
                        .tagHex("1F")
                        .valueHex("11BB")
                        .build()
                ))
                .tlv(Collections.singletonList(LntApiTlvDto.builder()
                        .tagHex("1B1A")
                        .valueHex("1AAF")
                        .build()
                ))
                .tests(Collections.singletonList(
                        LntApiCreateTestDto.builder()
                                .sender("123456789")
                                .text("Hello, World!")
                                .testIdTextLength(6)
                                .testIdTextCase(TestIdTextCase.MIXED)
                                .testIdTextType(TestIdTextType.NUMERIC)
                                .portedFromMnc(null)
                                .supplierId(6L)
                                .mcc("412")
                                .mnc("88")
                                .build()
                )).build();

        System.out.println(testRequestDto);

        LntApiCreateTestResponseDto testsCreated = telqApi.lnt.createTests(testRequestDto);
        System.out.println(testsCreated);

        Page<MtApiTestResultDto> mtTests = telqApi.mt.getTestPage(null, null, null);
        System.out.println(mtTests);

        System.out.println(telqApi.mt.getNetworks("631", null));
        System.out.println(telqApi.mt.getNetworks(null, "02"));

        PageConf pageConf = PageConf.builder()
                .page(1)
                .size(10)
                .build();
        Instant from = Instant.parse("2023-09-13T12:47:00Z");
        Instant to = Instant.parse("2023-10-13T12:47:00Z");
        Page<MtApiTestResultDto> page = telqApi.mt.getTestPage(pageConf, from, to);
    }

    @Test
    void suppliersSessionApiCalls_allSucceed() {
        TelqApi telqApi = new TelqApi(testApiKey, appId);

        // Get all sessions and suppliers
        Page<LntApiSessionDto> sessions = telqApi.lnt.getSessions(null);
        System.out.println(sessions);
        Page<LntApiSupplierDto> suppliers = telqApi.lnt.getSuppliers(null);
        System.out.println(suppliers);

        // Create session
        LntApiCreateOrUpdateSessionDto sessionDto = LntApiCreateOrUpdateSessionDto.builder()
                .destinationNpi((byte) 0x01)
                .destinationTon((byte) 0x00)
                .hostIp("testing.test")
                .systemId("systemId")
                .throughput(10)
                .hostPort(123)
                .systemType("123")
                .useSSL(true)
                .windowSize(10)
                .password("password")
                .build();
        LntApiSessionCreationResponseDto session = telqApi.lnt.createSession(sessionDto);
        System.out.println(session);

        // Update session
        sessionDto.setPassword("newPass");
        sessionDto.setSystemId("newSysId");
        sessionDto.setHostIp("new.testing.test");
        sessionDto.setEnabled(false);
        sessionDto.setWindowWaitTimeout(10_000L);
        sessionDto.setSmppSessionId(session.getSmppSessionId());
        telqApi.lnt.updateSession(sessionDto);

        // Get update individual session
        LntApiSessionDto sessionWithUserDto = telqApi.lnt.getSessionById(session.getSmppSessionId());
        System.out.println(sessionWithUserDto);


        // Create supplier
        LntApiCreateOrUpdateSupplierDto supplierDto = LntApiCreateOrUpdateSupplierDto.builder()
                .supplierName("supplierName")
                .attributeList(Arrays.asList(RouteAttribute.DLR, RouteAttribute.SPAM))
                .comment("comment 111")
                .routeType("Wholesale")
                .serviceType("434")
                .smppSessionId(session.getSmppSessionId())
                .tlvs(Arrays.asList(LntApiTlvDto.builder().tagHex("AAAA").valueHex("BBBB").build()))
                .udhs(Arrays.asList(LntApiUdhDto.builder().tagHex("AA").valueHex("BBBB").build()))
                .build();
        LntApiSupplierCreationResponseDto supplier = telqApi.lnt.createSupplier(supplierDto);
        System.out.println(supplier);

        // Update supplier
        supplierDto.setSupplierId(supplier.getSupplierId());
        supplierDto.setSupplierName("newSuppName");
        supplierDto.setRouteType("Premium");
        supplierDto.setServiceType("444");
        supplierDto.setTlvs(Arrays.asList(LntApiTlvDto.builder().tagHex("CCCC").valueHex("CCCC").build()));
        telqApi.lnt.updateSupplier(supplierDto);

        // Get individual supplier by id
        LntApiSupplierDto updatedSupplier = telqApi.lnt.getSupplierById(supplier.getSupplierId());
        System.out.println(updatedSupplier);

        // Create another session and reassign the supplier
        // Create session
        LntApiCreateOrUpdateSessionDto sessionDtoNew = LntApiCreateOrUpdateSessionDto.builder()
                .destinationNpi((byte) 0x01)
                .destinationTon((byte) 0x00)
                .hostIp("testing.test")
                .systemId("systemId")
                .throughput(10)
                .hostPort(123)
                .systemType("123")
                .useSSL(true)
                .windowSize(10)
                .password("password")
                .build();
        LntApiSessionCreationResponseDto sessionNew = telqApi.lnt.createSession(sessionDtoNew);
        System.out.println(session);

        Set<Long> supplierIds = new HashSet<>();
        supplierIds.add(supplier.getSupplierId());
        telqApi.lnt.assignSuppliersToSession(LntApiAssignSuppliersDto.builder().smppSessionId(sessionNew.getSmppSessionId()).supplierIds(supplierIds).build());
        LntApiSupplierDto reassignedSupplier = telqApi.lnt.getSupplierById(supplier.getSupplierId());
        System.out.println(reassignedSupplier);

        //Get suppliers-session status page
        Page<LntApiSessionsSuppliersStatusResponse> suppliersStatusResponsePage = telqApi.lnt.getSessionSuppliers(null);
        System.out.println(suppliersStatusResponsePage);

        // delete created sessions and suppliers
        telqApi.lnt.deleteSupplierById(supplier.getSupplierId());
        telqApi.lnt.deleteSessionById(session.getSmppSessionId());
        telqApi.lnt.deleteSessionById(sessionNew.getSmppSessionId());
    }
}