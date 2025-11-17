package org.b2code.loginhistory;

import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.Device;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.b2code.geoip.provider.GeoIpProvider;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.account.DeviceRepresentation;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.time.Instant;
import java.util.Optional;

@JBossLog
public class DefaultLoginHistoryProvider implements LoginHistoryProvider {

    private final KeycloakSession session;
    private final DeviceRepresentationProvider deviceRepresentationProvider;
    private final GeoIpProvider geoipProvider;
    private final LoginRecordRepository loginRecordRepository;

    public DefaultLoginHistoryProvider(KeycloakSession session) {
        log.tracef("Creating new %s", DefaultLoginHistoryProvider.class.getSimpleName());
        this.session = session;
        this.deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
        this.geoipProvider = session.getProvider(GeoIpProvider.class);
        this.loginRecordRepository = session.getProvider(LoginRecordRepository.class);
    }

    @Override
    public void track(DeviceRepresentation device, Event event) {
        LoginRecordEntity newRecord = generateRecord(device, event);
        KeycloakModelUtils.runJobInTransaction(session.getKeycloakSessionFactory(), sessionWithTransaction -> {
            sessionWithTransaction.getProvider(LoginRecordRepository.class).create(newRecord);
            log.debug("Successfully tracked login");
        });
    }

    @Override
    public boolean isKnownIp() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        return loginRecordRepository.isKnownIp(getAuthenticatedUserId(), ip);
    }

    @Override
    public boolean isUnknownDevice() {
        DeviceRepresentation deviceRep = deviceRepresentationProvider.deviceRepresentation();
        Device device = Device.fromDeviceRepresentation(deviceRep);
        return !loginRecordRepository.hasDeviceBeenUsed(getAuthenticatedUserId(), device);
    }

    @Override
    public boolean isUnknownLocation() {
        String ip = session.getContext().getConnection().getRemoteAddr();
        GeoIpInfo ipInfo = geoipProvider.getIpInfo(ip);
        return !loginRecordRepository.hasLocationBeenUsed(getAuthenticatedUserId(), ipInfo);
    }

    @Override
    public Optional<LoginRecordEntity> getLastLogin() {
        return loginRecordRepository.findLatestByUserId(getAuthenticatedUserId());
    }

    private LoginRecordEntity generateRecord(DeviceRepresentation deviceRep, Event event) {
        String ip = event.getIpAddress();
        Device device = Device.fromDeviceRepresentation(deviceRep);

        GeoIpInfo geoIpInfo = geoipProvider.getIpInfo(ip);
        return LoginRecordEntity.builder()
                .geoIpInfo(geoIpInfo)
                .device(device)
                .time(Instant.ofEpochMilli(event.getTime()))
                .userId(event.getUserId())
                .build();
    }

    private String getAuthenticatedUserId() {
        AuthenticationSessionModel authSession = session.getContext().getAuthenticationSession();
        if (authSession != null) {
            return authSession.getAuthenticatedUser().getId();
        }
        return null;
    }

    @Override
    public void close() {
        // NOOP
    }
}
