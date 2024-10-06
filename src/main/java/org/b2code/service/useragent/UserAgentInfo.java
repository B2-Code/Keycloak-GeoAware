package org.b2code.service.useragent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgentInfo {

    private String browser;
    private String browserType;
    private String browserMajorVersion;
    private String deviceType;
    private String platform;
    private String platformVersion;
}
