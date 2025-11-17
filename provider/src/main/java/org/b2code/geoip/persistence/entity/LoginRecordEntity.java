package org.b2code.geoip.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.vertx.runtime.jackson.InstantSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@NamedQueries({
        @NamedQuery(
                name = LoginRecordEntity.Q_BY_USER,
                query = "SELECT r FROM LoginRecordEntity r WHERE r.userId = :userId ORDER BY r.time DESC"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_BY_IP_AFTER,
                query = "SELECT r FROM LoginRecordEntity r WHERE r.geoIpInfo.ip = :ipAddress AND r.time >= :afterTime ORDER BY r.time DESC"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_IS_KNOWN_IP,
                query = "SELECT COUNT(r) FROM LoginRecordEntity r WHERE r.userId = :userId AND r.geoIpInfo.ip = :ipAddress"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_DELETE_BY_USER,
                query = "DELETE FROM LoginRecordEntity r WHERE r.userId = :userId"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_DELETE_BY_REALM,
                query = "DELETE FROM LoginRecordEntity r WHERE r.userId IN (SELECT u.id FROM UserEntity u WHERE u.realmId = :realmId)"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_CLEANUP,
                query = "DELETE FROM LoginRecordEntity r WHERE r.time < :cutoffTime"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_HAS_DEVICE,
                query = "SELECT CASE WHEN EXISTS (SELECT 1 FROM LoginRecordEntity l WHERE l.userId = :userId AND l.device.os = :os AND l.device.osVersion = :osVersion AND l.device.browser = :browser) THEN true ELSE false END"
        ),
        @NamedQuery(
                name = LoginRecordEntity.Q_LOCATIONS_IN_BBOX,
                query = "SELECT r FROM LoginRecordEntity r WHERE r.userId = :userId AND r.geoIpInfo IS NOT NULL AND r.geoIpInfo.latitude BETWEEN :minLat AND :maxLat AND r.geoIpInfo.longitude BETWEEN :minLon AND :maxLon ORDER BY r.time DESC"
        )
})
@Table(
        name = "geoaware_login_record",
        indexes = {
                @Index(name = "idx_login_record_userid", columnList = "USER_ID"),
                @Index(name = "idx_login_record_ip_time", columnList = "IP_ADDRESS,TIMESTAMP"),
                @Index(name = "idx_login_record_time", columnList = "TIMESTAMP"),
                @Index(name = "idx_login_record_user_lat_lon_time", columnList = "USER_ID,LATITUDE,LONGITUDE,TIMESTAMP")
        }
)
public class LoginRecordEntity {

    public static final String Q_BY_USER = "geoaware_getLoginRecordsByUserId";
    public static final String Q_BY_IP_AFTER = "geoaware_getLoginRecordByIpAndTimestampAfter";
    public static final String Q_IS_KNOWN_IP = "geoaware_isKnownIp";
    public static final String Q_DELETE_BY_USER = "geoaware_deleteLoginRecordsByUserId";
    public static final String Q_DELETE_BY_REALM = "geoaware_deleteLoginRecordsByRealmId";
    public static final String Q_CLEANUP = "geoaware_cleanupOldLoginRecords";
    public static final String Q_HAS_DEVICE = "geoaware_hasDeviceBeenUsed";
    public static final String Q_LOCATIONS_IN_BBOX = "geoaware_findLocationsInBoundingBox";

    @Id
    @Column(name = "ID", length = 36, nullable = false)
    private String id;

    @Column(name = "USER_ID", nullable = false, length = 36)
    private String userId;

    @JsonSerialize(using = InstantSerializer.class)
    @Column(name = "TIMESTAMP", nullable = false)
    private Instant time;

    @Embedded
    private Device device;

    @Embedded
    private GeoIpInfo geoIpInfo;

    @JsonIgnore
    public String getIp() {
        return geoIpInfo.getIp();
    }

}
