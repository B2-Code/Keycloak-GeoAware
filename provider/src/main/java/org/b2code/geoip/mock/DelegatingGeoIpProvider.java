package org.b2code.geoip.mock;

import lombok.RequiredArgsConstructor;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoIpProvider;

@RequiredArgsConstructor
public class DelegatingGeoIpProvider implements GeoIpProvider {

    private final GeoIpProvider delegate;
    private final String mockIp;

    @Override
    public GeoIpInfo getIpInfo(String ipAddress) {
        if (mockIp != null && !mockIp.isBlank()) {
            return delegate.getIpInfo(mockIp);
        } else {
            return delegate.getIpInfo(ipAddress);
        }
    }

    @Override
    public void close() {
        if (delegate != null) {
            delegate.close();
        }
    }
}
