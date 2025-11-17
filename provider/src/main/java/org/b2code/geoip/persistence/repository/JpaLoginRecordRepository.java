package org.b2code.geoip.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JBossLog
@RequiredArgsConstructor
public class JpaLoginRecordRepository implements LoginRecordRepository {

    private final KeycloakSession session;
    private final int compareLimit;

    private EntityManager em() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @Override
    public LoginRecordEntity create(LoginRecordEntity loginRecord) {
        if (loginRecord.getId() == null) {
            loginRecord.setId(KeycloakModelUtils.generateId());
        }
        em().persist(loginRecord);
        return loginRecord;
    }

    @Override
    public Optional<LoginRecordEntity> findLatestByUserId(String userId) {
        if (userId == null) {
            return Optional.empty();
        }
        List<LoginRecordEntity> list = em()
                .createNamedQuery(LoginRecordEntity.Q_BY_USER, LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public Optional<LoginRecordEntity> findByIpAndTimestampAfter(String ipAddress, Instant timestamp) {
        if (ipAddress == null || timestamp == null) {
            return Optional.empty();
        }
        List<LoginRecordEntity> list = em()
                .createNamedQuery(LoginRecordEntity.Q_BY_IP_AFTER, LoginRecordEntity.class)
                .setParameter("ipAddress", ipAddress)
                .setParameter("afterTime", timestamp)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public List<LoginRecordEntity> findAllByUserId(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return em()
                .createNamedQuery(LoginRecordEntity.Q_BY_USER, LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getResultList();
    }

    @Override
    public boolean isKnownIp(String userId, String ipAddress) {
        if (userId == null || ipAddress == null) {
            return false;
        }
        Long count = em()
                .createNamedQuery(LoginRecordEntity.Q_IS_KNOWN_IP, Long.class)
                .setParameter("userId", userId)
                .setParameter("ipAddress", ipAddress)
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void deleteByUserId(String userId) {
        if (userId == null) {
            return;
        }
        em().createNamedQuery(LoginRecordEntity.Q_DELETE_BY_USER)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public void deleteByRealmId(String realmId) {
        if (realmId == null) {
            return;
        }
        em().createNamedQuery(LoginRecordEntity.Q_DELETE_BY_REALM)
                .setParameter("realmId", realmId)
                .executeUpdate();
    }

    @Override
    public long cleanupOldRecords(int hoursToKeep) {
        if (hoursToKeep < 0) {
            return 0;
        }
        Instant cutoffTime = Instant.ofEpochSecond(Time.currentTime())
                .minus(Duration.ofHours(hoursToKeep));
        return em().createNamedQuery(LoginRecordEntity.Q_CLEANUP)
                .setParameter("cutoffTime", cutoffTime)
                .executeUpdate();
    }

    @Override
    public boolean hasDeviceBeenUsed(String userId, Device device) {
        if (userId == null || device == null) {
            return false;
        }
        TypedQuery<Boolean> q = em()
                .createNamedQuery(LoginRecordEntity.Q_HAS_DEVICE, Boolean.class)
                .setParameter("userId", userId)
                .setParameter("os", device.getOs())
                .setParameter("osVersion", device.getOsVersion())
                .setParameter("browser", device.getBrowser())
                .setHint(AvailableHints.HINT_READ_ONLY, true);
        Boolean result = q.getSingleResult();
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean hasLocationBeenUsed(String userId, GeoIpInfo location) {
        if (userId == null || location == null) {
            return false;
        }

        BoundingBox bbox = GeoCalculator.calculateBoundingBox(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracyRadius()
        );

        return em()
                .createNamedQuery(LoginRecordEntity.Q_LOCATIONS_IN_BBOX, LoginRecordEntity.class)
                .setParameter("userId", userId)
                .setParameter("minLat", bbox.getMinLatitude())
                .setParameter("maxLat", bbox.getMaxLatitude())
                .setParameter("minLon", bbox.getMinLongitude())
                .setParameter("maxLon", bbox.getMaxLongitude())
                .setHint(AvailableHints.HINT_READ_ONLY, true)
                .setMaxResults(compareLimit)
                .getResultStream()
                .map(LoginRecordEntity::getGeoIpInfo)
                .filter(Objects::nonNull)
                .anyMatch(info -> GeoCalculator.circlesOverlap(location, info));
    }

    @Override
    public void close() {
        // NOOP
    }
}
