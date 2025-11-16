package org.b2code.geoip.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@JBossLog
@RequiredArgsConstructor
public class DefaultGeoIpCacheProvider implements GeoIpCacheProvider {

    private final KeycloakSession session;
    private final int cacheDurationMinutes;

    @Override
    public void put(KeycloakSession session, String ipAddress, GeoIpInfo geoIpInfo) {
        // NOOP - caching is handled when saving the LoginRecordEntity
    }

    @Override
    public Optional<GeoIpInfo> get(KeycloakSession session, String ipAddress) {
        LoginRecordRepository loginRecordRepository = this.session.getProvider(LoginRecordRepository.class);
        log.tracef("Getting '%s' from cache", ipAddress);
        Instant afterTime = Instant.ofEpochMilli(Time.currentTimeMillis()).minus(cacheDurationMinutes, ChronoUnit.MINUTES);
        return loginRecordRepository.findByIpAndTimestampAfter(ipAddress, afterTime).map(LoginRecordEntity::getGeoIpInfo);
    }

    @Override
    public void close() {
        // NOOP
    }
}