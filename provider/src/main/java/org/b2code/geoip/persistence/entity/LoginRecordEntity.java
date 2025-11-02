package org.b2code.geoip.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
                name = "geoaware_getLatestLoginRecordByUserId",
                query = "SELECT r FROM LoginRecordEntity r WHERE r.userId = :userId ORDER BY r.time DESC"
        ),
        @NamedQuery(
                name = "geoaware_isKnownIp",
                query = "SELECT COUNT(r) FROM LoginRecordEntity r WHERE r.userId = :userId AND r.geoIpInfo.ip = :ipAddress"
        ),
        @NamedQuery(
                name = "geoaware_deleteLoginRecordsByUserId",
                query = "DELETE FROM LoginRecordEntity r WHERE r.userId = :userId"
        ),
        @NamedQuery(
                name = "geoaware_deleteLoginRecordsByRealmId",
                query = "DELETE FROM LoginRecordEntity r WHERE r.userId IN (SELECT u.id FROM UserEntity u WHERE u.realmId = :realmId)"
        ),
        @NamedQuery(
                name = "geoaware_cleanupOldLoginRecords",
                query = "DELETE FROM LoginRecordEntity r WHERE r.time < :cutoffTime"
        ),
        @NamedQuery(
                name = "geoaware_hasDeviceBeenUsed",
                query = "SELECT CASE WHEN EXISTS (SELECT 1 FROM LoginRecordEntity l WHERE l.userId = :userId AND l.device.os = :os AND l.device.osVersion = :osVersion AND l.device.browser = :browser) THEN true ELSE false END"
        ),
        @NamedQuery(
                name = "geoaware_findLocationsInBoundingBox",
                query = "SELECT r FROM LoginRecordEntity r WHERE r.userId = :userId AND r.geoIpInfo IS NOT NULL AND r.geoIpInfo.latitude BETWEEN :minLat AND :maxLat AND r.geoIpInfo.longitude BETWEEN :minLon AND :maxLon"
        )
})
@Table(
        name = "geoip_login_record",
        indexes = {
                @Index(name = "idx_login_record_userid", columnList = "USER_ID"),
        }
)
public class LoginRecordEntity {

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
    @JsonProperty(value = "geoip")
    private GeoIpInfo geoIpInfo;

    @JsonIgnore
    public String getIp() {
        return geoIpInfo.getIp();
    }

}
