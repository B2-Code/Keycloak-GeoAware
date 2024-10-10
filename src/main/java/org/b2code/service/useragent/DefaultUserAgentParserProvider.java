package org.b2code.service.useragent;


import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.google.common.base.Stopwatch;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class DefaultUserAgentParserProvider implements UserAgentParserProvider {

    private final UserAgentParser userAgentParser;

    public DefaultUserAgentParserProvider(UserAgentParser userAgentParser) {
        this.userAgentParser = userAgentParser;
    }

    public UserAgentInfo parse(String userAgent) {
        if (userAgentParser == null) {
            return new UserAgentInfo();
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        Capabilities parsedUserAgent = userAgentParser.parse(userAgent);
        log.debugf("User agent parsing took %s", stopwatch.stop());
        return UserAgentInfo.builder()
                .browser(parsedUserAgent.getBrowser())
                .browserType(parsedUserAgent.getBrowserType())
                .browserMajorVersion(parsedUserAgent.getBrowserMajorVersion())
                .deviceType(parsedUserAgent.getDeviceType())
                .platform(parsedUserAgent.getPlatform())
                .platformVersion(parsedUserAgent.getPlatformVersion())
                .build();
    }

    @Override
    public void close() {
        // NOOP
    }
}

