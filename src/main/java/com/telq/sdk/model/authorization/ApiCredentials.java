package com.telq.sdk.model.authorization;

import com.telq.sdk.service.authorization.RestV2AuthorizationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * {@link ApiCredentials} are used to store app_id and app_key and pass them around more easily.
 * {@link RestV2AuthorizationService} relies on it
 */
@Data
@AllArgsConstructor
@Builder
public class ApiCredentials {

    @NonNull
    private String appId;

    @NonNull
    private String appKey;

    public boolean initialised() {
        return !appId.equals("") && !appKey.equals("");
    }

}
