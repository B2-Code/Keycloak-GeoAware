package org.b2code.geoip.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.account.DeviceRepresentation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class Device {

    @Column(name = "DEVICE_OS", length = 50)
    private String os;

    @Column(name = "DEVICE_OS_VERSION", length = 20)
    private String osVersion;

    @Column(name = "DEVICE_BROWSER", length = 50)
    private String browser;

    @Column(name = "DEVICE_TYPE", length = 30)
    private String deviceType;

    @JsonProperty(value = "mobile")
    @Column(name = "DEVICE_IS_MOBILE")
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
