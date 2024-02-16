package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageConf {
    public enum Order {
        ASC, DESC
    }
    Integer page;
    Integer size;
    Order order;
}
