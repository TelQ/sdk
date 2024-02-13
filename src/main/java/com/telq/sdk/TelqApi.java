package com.telq.sdk;

import com.telq.sdk.clients.LiveNumberTestingClient;
import com.telq.sdk.clients.LntClient;
import com.telq.sdk.clients.ManualTestingClient;
import com.telq.sdk.clients.TelQTestClient;

public class TelqApi {
    public final LiveNumberTestingClient lnt;
    public final ManualTestingClient mt;

    public TelqApi(String appKey, String appId) {
        this.mt = TelQTestClient.getInstance(appKey, appId);
        this.lnt = new LntClient(TelQTestClient.getHttpClient());
    }
}
