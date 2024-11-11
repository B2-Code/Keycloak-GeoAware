package org.b2code.service.loginhistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@JBossLog
public class DefaultLoginHistoryProvider implements LoginHistoryProvider {

    public static final String USER_ATTRIBUTE_LAST_IPS = "loginHistoryRecord";

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final KeycloakSession session;

    private final DeviceRepresentationProvider deviceRepresentationProvider;

    private final GeoipDatabaseAccessProvider geoipDatabaseAccessProvider;

    private final Duration retentionTime;

    private final int maxRecords;

    // The list is sorted by time, newest first
    private final List<LoginRecord> loginRecords;

    public DefaultLoginHistoryProvider(KeycloakSession session, Duration retentionTime, int maxRecords) {
        log.tracef("Creating new %s", DefaultLoginHistoryProvider.class.getSimpleName());
        this.session = session;
        this.retentionTime = retentionTime;
        this.maxRecords = maxRecords;
        this.deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
        this.geoipDatabaseAccessProvider = session.getProvider(GeoipDatabaseAccessProvider.class);
        this.loginRecords = getLoginRecords();
    }

    public void track() {
        Stream<LoginRecord> newRecords = Stream.concat(Stream.of(generateRecord()), getHistoryStream()).limit(maxRecords);
        setLoginRecords(newRecords);
        log.trace("Successfully tracked login");
    }

    public boolean isKnownIp() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        return getHistoryStream().anyMatch(r -> r.getIp().equals(ip));
    }

    public boolean isKnownDevice() {
        DeviceRepresentation deviceRep = deviceRepresentationProvider.deviceRepresentation();
        LoginRecord.Device device = LoginRecord.Device.fromDeviceRepresentation(deviceRep);
        return getHistoryStream().anyMatch(r -> r.getDevice().equals(device));
    }

    public boolean isKnownLocation() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        GeoipDatabaseAccessProvider provider = session.getProvider(GeoipDatabaseAccessProvider.class);
        GeoIpInfo ipInfo = provider.getIpInfo(ip);
        return getHistoryStream().anyMatch(r -> r.getGeoIpInfo().radiusOverlapsWith(ipInfo));
    }

    public Optional<LoginRecord> getLastLogin() {
        return getHistory().isEmpty() ? Optional.empty() : Optional.of(getHistory().getFirst());
    }

    public List<LoginRecord> getHistory() {
        return this.loginRecords;
    }

    public Stream<LoginRecord> getHistoryStream() {
        return this.loginRecords.stream();
    }

    private LoginRecord generateRecord() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        DeviceRepresentation device = deviceRepresentationProvider.deviceRepresentation();
        GeoIpInfo geoIpInfo = geoipDatabaseAccessProvider.getIpInfo(ip);
        return LoginRecord.builder()
                .geoIpInfo(geoIpInfo)
                .device(LoginRecord.Device.fromDeviceRepresentation(device))
                .time(Instant.now())
                .build();
    }

    private List<LoginRecord> getLoginRecords() {
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        Instant now = Instant.now();
        return user.getAttributeStream(USER_ATTRIBUTE_LAST_IPS)
                .map(e -> {
                    try {
                        return objectMapper.readValue(e, LoginRecord.class);
                    } catch (JsonProcessingException ex) {
                        log.errorf("Failed to parse last IP record: %s", ex.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(r -> r.getTime().isAfter(now.minus(retentionTime)))
                .sorted(Comparator.comparing(LoginRecord::getTime).reversed())
                .toList();
    }

    private void setLoginRecords(Stream<LoginRecord> newRecords) {
        List<String> newValues = newRecords
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
        session.getContext().getAuthenticationSession().getAuthenticatedUser().setAttribute(USER_ATTRIBUTE_LAST_IPS, newValues);
    }

    @Override
    public void close() {
        // NOOP
    }
}
