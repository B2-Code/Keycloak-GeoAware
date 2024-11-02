package org.b2code.service.iphistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.b2code.service.mail.EmailHelper;
import org.keycloak.common.util.Time;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JBossLog
public class DefaultIpHistoryProvider implements IpHistoryProvider {

    public static final String USER_ATTRIBUTE_LAST_IPS = "ipHistoryRecord";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final KeycloakSession session;

    private final DeviceRepresentationProvider deviceRepresentationProvider;

    private final int retentionTimeHours;

    public DefaultIpHistoryProvider(KeycloakSession session, int retentionTimeHours) {
        log.tracef("Creating new %s", DefaultIpHistoryProvider.class.getSimpleName());
        this.session = session;
        this.retentionTimeHours = retentionTimeHours;
        this.deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
    }

    /**
     * Persists the IP address of a user in the history.
     */
    public void track() {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        String ip = session.getContext().getConnection().getRemoteAddr();

        List<LastIpRecord> lastIps = getLastIps(user);
        boolean knownIp = updateRecords(lastIps, ip);
        setLastIps(user, lastIps);

        if (!knownIp) {
            log.debugf("New IP for user %s (%s), sending notification mail", user.getUsername(), ip);
            GeoipDatabaseAccessProvider geoipDatabaseAccessProvider = session.getProvider(GeoipDatabaseAccessProvider.class);
            DeviceRepresentationProvider deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
            GeoIpInfo geoIpInfo = geoipDatabaseAccessProvider.getIpInfo(ip);
            EmailHelper.sendNewIpEmail(geoIpInfo, deviceRepresentationProvider.deviceRepresentation(), session, user, realm);
        }
    }

    /**
     * Updates the list of IP records by removing old records and adding the new IP.
     *
     * @param lastIps the list of last IP records
     * @param ip      the new IP address to be added
     * @return true if the IP address was already known, false otherwise
     */
    private boolean updateRecords(List<LastIpRecord> lastIps, String ip) {
        long now = Time.currentTimeMillis();
        DeviceRepresentation device = deviceRepresentationProvider.deviceRepresentation();
        lastIps.removeIf(r -> now - r.getTime() > (long) retentionTimeHours * 60 * 60 * 1000);
        boolean knownIp = lastIps.removeIf(r -> r.getIp().equals(ip));
        lastIps.add(LastIpRecord.builder()
                .ip(ip)
                .device(LastIpRecord.Device.builder()
                        .deviceType(device.getDevice())
                        .os(device.getOs())
                        .osVersion(device.getOsVersion())
                        .browser(device.getBrowser())
                        .isMobile(device.isMobile())
                        .build())
                .time(now)
                .build());
        return knownIp;
    }

    private List<LastIpRecord> getLastIps(UserModel user) {
        return user.getAttributeStream(USER_ATTRIBUTE_LAST_IPS)
                .map(e -> {
                    try {
                        return objectMapper.readValue(e, LastIpRecord.class);
                    } catch (JsonProcessingException ex) {
                        log.errorf("Failed to parse last IP record: %s", ex.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setLastIps(UserModel user, List<LastIpRecord> lastIps) {
        List<String> newValues = lastIps.stream()
                .map(e -> {
                    try {
                        return objectMapper.writeValueAsString(e);
                    } catch (JsonProcessingException ex) {
                        log.errorf("Failed to write last IP record: %s", ex.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        log.debugf("Writing user attributes %s = %s", USER_ATTRIBUTE_LAST_IPS, newValues);
        user.setAttribute(USER_ATTRIBUTE_LAST_IPS, newValues);
    }

    @Override
    public void close() {
        // NOOP
    }
}
