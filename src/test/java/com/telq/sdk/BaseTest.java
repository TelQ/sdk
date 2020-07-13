package com.telq.sdk;


import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;

public class BaseTest {

    protected final String token = "SomeBigLongVeryBigLongHugeOriginalToken";
    protected final int ttl = 86400;

    protected final String appKey = "o-/PTc)^5d+z/hLWK=#bs07y^wp+5yg%$C9";
    protected final String appId = "1447";

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

}
