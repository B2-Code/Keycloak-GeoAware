package org.b2code.geoip.persistence.repository;

import org.b2code.geoip.persistence.entity.Device;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.keycloak.provider.Provider;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface LoginRecordRepository extends Provider {

    LoginRecordEntity create(LoginRecordEntity loginRecord);

    Optional<LoginRecordEntity> findLatestByUserId(String userId);

    Optional<LoginRecordEntity> findByIpAndTimestampAfter(String ipAddress, Instant timestamp);

    Collection<LoginRecordEntity> findAllByUserId(String userId);

    boolean isKnownIp(String userId, String ipAddress);

    void deleteByUserId(String userId);

    void deleteByRealmId(String realmId);

    void cleanupOldRecords(int hoursToKeep);

    boolean hasDeviceBeenUsed(String userId, Device device);

    boolean hasLocationBeenUsed(String userId, GeoIpInfo location);
}
