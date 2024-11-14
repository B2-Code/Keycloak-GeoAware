package org.b2code.service.loginhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.vertx.runtime.jackson.InstantSerializer;
import lombok.*;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.representations.account.DeviceRepresentation;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRecord {

    @JsonSerialize(using = InstantSerializer.class)
    private Instant time;
    private Device device;
    @JsonProperty(value = "geoip")
    private GeoIpInfo geoIpInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Device {
        private String os;
        private String osVersion;
        private String browser;
        private String deviceType;
        @JsonProperty(value = "mobile")
        private Boolean isMobile;

        public static Device fromDeviceRepresentation(DeviceRepresentation device) {
            if (device == null) {
                return new Device();
            }
            return builder()
                    .deviceType(device.getDevice())
                    .os(device.getOs())
                    .osVersion(device.getOsVersion())
                    .browser(device.getBrowser())
                    .isMobile(device.isMobile())
                    .build();
        }
    }

    @JsonIgnore
    public String getIp() {
        return geoIpInfo.getIp();
    }

}
