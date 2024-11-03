package org.b2code.service.loginhistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.util.Time;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JBossLog
public class DefaultLoginHistoryProvider implements LoginHistoryProvider {

    public static final String USER_ATTRIBUTE_LAST_IPS = "loginHistoryRecord";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final KeycloakSession session;

    private final DeviceRepresentationProvider deviceRepresentationProvider;

    private final int retentionTimeHours;

    private final int maxRecords;

    private List<LoginRecord> loginRecords;

    public DefaultLoginHistoryProvider(KeycloakSession session, int retentionTimeHours, int maxRecords) {
        log.tracef("Creating new %s", DefaultLoginHistoryProvider.class.getSimpleName());
        this.session = session;
        this.retentionTimeHours = retentionTimeHours;
        this.maxRecords = maxRecords;
        this.deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
        getLoginRecords();
    }

    public void track() {
        updateRecords();
        log.trace("Successfully tracked login");
    }

    private void updateRecords() {
        long now = Time.currentTime();
        loginRecords.removeIf(r -> now - r.getTime() > (long) retentionTimeHours * 60 * 60);
        loginRecords.addFirst(generateRecord(now));
        if (loginRecords.size() > maxRecords) {
            loginRecords = loginRecords.stream().sorted(Comparator.comparingLong(LoginRecord::getTime).reversed()).limit(maxRecords).toList();
        }
        setLoginRecords();
    }

    public boolean isKnownIp() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        return loginRecords.stream().anyMatch(r -> r.getIp().equals(ip));
    }

    public boolean isKnownDevice() {
        DeviceRepresentation deviceRep = deviceRepresentationProvider.deviceRepresentation();
        LoginRecord.Device device = LoginRecord.Device.fromDeviceRepresentation(deviceRep);
        return loginRecords.stream().anyMatch(r -> r.getDevice().equals(device));
    }

    public List<LoginRecord> getHistory() {
        return Collections.unmodifiableList(loginRecords);
    }

    private LoginRecord generateRecord(long now) {
        String ip = session.getContext().getConnection().getRemoteAddr();
        DeviceRepresentation device = deviceRepresentationProvider.deviceRepresentation();
        return LoginRecord.builder()
                .ip(ip)
                .device(LoginRecord.Device.fromDeviceRepresentation(device))
                .time(now)
                .build();
    }

    private void getLoginRecords() {
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        this.loginRecords = user.getAttributeStream(USER_ATTRIBUTE_LAST_IPS)
                .map(e -> {
                    try {
                        return objectMapper.readValue(e, LoginRecord.class);
                    } catch (JsonProcessingException ex) {
                        log.errorf("Failed to parse last IP record: %s", ex.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setLoginRecords() {
        List<String> newValues = loginRecords.stream()
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
