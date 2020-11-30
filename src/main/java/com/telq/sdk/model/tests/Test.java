package com.telq.sdk.model.tests;

import com.telq.sdk.model.network.DestinationNetwork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * When tests are initiated {@link Test} represents an item in the list received. It's a description of a test
 * made.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Test {

        private DestinationNetwork destinationNetwork;

        private String errorMessage;

        private Long id;

        private String phoneNumber;

        private String testIdText;

}
