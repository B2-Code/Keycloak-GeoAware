package org.b2code.service.loginhistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.representations.account.DeviceRepresentation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRecord {

    private Long time;
    private Device device;
    @JsonProperty(value = "geoip")
    private GeoIpInfo geoIpInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Device {
        private String os;
        private String osVersion;
        private String browser;
        private String deviceType;
        @JsonProperty(value = "mobile")
        private boolean isMobile;

        public static Device fromDeviceRepresentation(DeviceRepresentation device) {
            return builder()
                    .deviceType(device.getDevice())
                    .os(device.getOs())
                    .osVersion(device.getOsVersion())
                    .browser(device.getBrowser())
                    .isMobile(device.isMobile())
                    .build();
        }
    }

    public String getIp() {
        return geoIpInfo.getIp();
    }

}
