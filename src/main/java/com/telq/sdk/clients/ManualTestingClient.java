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
    List<Network> getNetworks() throws Exception;

    List<Test> initiateNewTests(TestRequest testRequest) throws Exception;

    Result getTestResult(Long testId) throws Exception;

    Page<MtApiTestResultDto> getTestResults(PageConf pageConf, Instant from, Instant to);
}
