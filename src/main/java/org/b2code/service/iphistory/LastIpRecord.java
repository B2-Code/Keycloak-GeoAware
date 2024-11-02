package org.b2code.service.iphistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LastIpRecord {
    private String ip;
    private Long time;
    private Device device;

    @Value
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Device {
        String os;
        String osVersion;
        String browser;
        String deviceType;
        @JsonProperty(value="mobile")
        boolean isMobile;
    }
}
