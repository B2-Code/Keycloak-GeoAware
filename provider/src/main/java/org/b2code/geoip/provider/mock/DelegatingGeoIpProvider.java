package org.b2code.geoip.provider.mock;

import lombok.RequiredArgsConstructor;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.provider.GeoIpProvider;

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
