package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageConf {
    enum Order {
        ASC, DESC
    }
    Integer page;
    Instant size;
    Order order;
}
