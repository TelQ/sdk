package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LntApiUserDto {
    private Long id;
    private String email;
    private String username;
    private String fullName;
}
