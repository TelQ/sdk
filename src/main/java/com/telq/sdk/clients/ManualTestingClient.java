package com.telq.sdk.clients;

import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.tests.Test;
import com.telq.sdk.model.tests.TestRequest;
import com.telq.sdk.model.v3.lnt.Page;
import com.telq.sdk.model.v3.lnt.PageConf;
import com.telq.sdk.model.v3.mt.MtApiTestResultDto;

import java.time.Instant;
import java.util.List;

public interface ManualTestingClient {
    List<Network> getNetworks();

    List<Network> getNetworks(String mcc, String mnc);
    List<Network> getNetworks(String mcc, String mnc, String portedFromMnc);

    List<Test> createTests(TestRequest testRequest);

    MtApiTestResultDto getTestById(Long testId);

    Page<MtApiTestResultDto> getTestPage(PageConf pageConf, Instant from, Instant to);
}
