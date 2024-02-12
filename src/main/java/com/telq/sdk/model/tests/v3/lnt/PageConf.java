package com.telq.sdk.model.tests.v3.lnt;

import java.time.Instant;

public class PageConf {
    enum Order {
        ASC, DESC
    }
    Integer page;
    Instant size;
    Order order;
}
