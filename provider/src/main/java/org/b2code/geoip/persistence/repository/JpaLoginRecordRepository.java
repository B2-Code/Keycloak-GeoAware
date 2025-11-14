package org.b2code.geoip.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.b2code.geoip.helper.BoundingBox;
import org.b2code.geoip.helper.GeoCalculator;
import org.b2code.geoip.persistence.entity.Device;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.hibernate.jpa.AvailableHints;
import org.keycloak.common.util.Time;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaLoginRecordRepository implements LoginRecordRepository {

    private final KeycloakSession session;

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @Override
    public LoginRecordEntity create(LoginRecordEntity loginRecord) {
        if (loginRecord.getId() == null) {
            String id = KeycloakModelUtils.generateId();
            loginRecord.setId(id);
        }
        EntityManager em = getEntityManager();
        em.persist(loginRecord);
        em.flush();
        return loginRecord;
    }

    @Override
    public Optional<LoginRecordEntity> findLatestByUserId(String userId) {
        TypedQuery<LoginRecordEntity> query = getEntityManager()
                .createNamedQuery("geoaware_getLoginRecordsByUserId", LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .setMaxResults(1);
        List<LoginRecordEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public Optional<LoginRecordEntity> findByIpAndTimestampAfter(String ipAddress, Instant timestamp) {
        TypedQuery<LoginRecordEntity> query = getEntityManager()
                .createNamedQuery("geoaware_getLoginRecordByIpAndTimestampAfter", LoginRecordEntity.class)
                .setParameter("ipAddress", ipAddress)
                .setParameter("afterTime", timestamp)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .setMaxResults(1);
        List<LoginRecordEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<LoginRecordEntity> findAllByUserId(String userId) {
        return getEntityManager()
                .createNamedQuery("geoaware_getLoginRecordsByUserId", LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getResultList();
    }

    @Override
    public boolean isKnownIp(String userId, String ipAddress) {
        Long count = getEntityManager()
                .createNamedQuery("geoaware_isKnownIp", Long.class)
                .setParameter("userId", userId)
                .setParameter("ipAddress", ipAddress)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void deleteByUserId(String userId) {
        getEntityManager()
                .createNamedQuery("geoaware_deleteLoginRecordsByUserId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public void deleteByRealmId(String realmId) {
        getEntityManager()
                .createNamedQuery("geoaware_deleteLoginRecordsByRealmId")
                .setParameter("realmId", realmId)
                .executeUpdate();
    }

    @Override
    public void cleanupOldRecords(int hoursToKeep) {
        Instant cutoffTime = Instant.ofEpochSecond(Time.currentTime()).minus(Duration.ofHours(hoursToKeep));
        getEntityManager()
                .createNamedQuery("geoaware_cleanupOldLoginRecords")
                .setParameter("cutoffTime", cutoffTime)
                .executeUpdate();
    }

    @Override
    public boolean hasDeviceBeenUsed(String userId, Device device) {
        return getEntityManager()
                .createNamedQuery("geoaware_hasDeviceBeenUsed", Boolean.class)
                .setParameter("userId", userId)
                .setParameter("os", device.getOs())
                .setParameter("osVersion", device.getOsVersion())
                .setParameter("browser", device.getBrowser())
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getSingleResult();
    }

    @Override
    public boolean hasLocationBeenUsed(String userId, GeoIpInfo location) {
        if (location == null) {
            return false;
        }

        BoundingBox bbox = GeoCalculator.calculateBoundingBox(location.getLatitude(), location.getLongitude(), location.getAccuracyRadius());

        // first get candidates in bounding box
        List<LoginRecordEntity> candidates = getEntityManager()
                .createNamedQuery("geoaware_findLocationsInBoundingBox", LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setParameter("minLat", bbox.getMinLatitude())
                .setParameter("maxLat", bbox.getMaxLatitude())
                .setParameter("minLon", bbox.getMinLongitude())
                .setParameter("maxLon", bbox.getMaxLongitude())
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getResultList();

        // then do precise check
        return candidates.stream().anyMatch(record -> GeoCalculator.isLocationWithinRadius(location, record.getGeoIpInfo()));
    }

    @Override
    public void close() {
        // NOOP
    }
}
