package com.telq.sdk;

import com.telq.sdk.clients.LiveNumberTestingClient;
import com.telq.sdk.clients.LntClient;
import com.telq.sdk.clients.ManualTestingClient;
import com.telq.sdk.clients.TelQTestClient;

public class TelqApi {
    public final LiveNumberTestingClient lnt;
    public final ManualTestingClient mt;

    public TelqApi(String appKey, String appId) {
        TelQTestClient mtClient = TelQTestClient.getInstance(appKey, appId);
        this.mt = mtClient;
        this.lnt = new LntClient(TelQTestClient.getHttpClient(), mtClient.getAuthorizationService());
    }
}
