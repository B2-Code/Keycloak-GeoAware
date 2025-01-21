package org.b2code.geoip.ipdata;

import com.google.common.base.Stopwatch;
import io.ipdata.client.model.IpdataModel;
import io.ipdata.client.service.IpdataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoipProvider;

import java.net.InetAddress;

@JBossLog
@RequiredArgsConstructor
public class IpDataProvider implements GeoipProvider {

    private final IpdataService ipdataService;

    public GeoIpInfo getIpInfo(String ipAddress) {
        if (null == ipdataService) {
            log.warn("IpData provider not initialized");
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        InetAddress inetAddress = getInetAddress(ipAddress);
        if (null == inetAddress || inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
            log.debugf("Skipping IpData lookup for IP address '%s'", ipAddress);
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            IpdataModel ipdataModel = ipdataService.ipdata(ipAddress);
            log.debugf("IpData lookup took %s", stopwatch.stop());
            return IpDataHelper.map(ipdataModel, ipAddress);
        } catch (Exception e) {
            log.error("Error while performing GeoIP lookup", e);
        }

        return GeoIpInfo.builder().ip(ipAddress).build();
    }

    private InetAddress getInetAddress(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress);
        } catch (Exception e) {
            log.errorf("Failed to get InetAddress for %s", ipAddress);
            return null;
        }
    }

    @Override
    public void close() {
        // NOOP
    }
}
