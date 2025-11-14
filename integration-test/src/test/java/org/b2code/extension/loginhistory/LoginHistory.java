package org.b2code.extension.loginhistory;

import lombok.RequiredArgsConstructor;
import org.b2code.geoip.persistence.entity.Device;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LoginHistory implements AutoCloseable {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    private Connection connection;

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        return connection;
    }

    public List<LoginRecordEntity> getAllByUserId(String userId) {
        List<LoginRecordEntity> results = new ArrayList<>();
        final String sql = "SELECT * FROM geoaware_login_record WHERE USER_ID = ? ORDER BY TIMESTAMP DESC";
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Instant time = rs.getTimestamp("TIMESTAMP").toInstant();
                        Device device = Device.builder()
                                .os(rs.getString("DEVICE_OS"))
                                .osVersion(rs.getString("DEVICE_OS_VERSION"))
                                .browser(rs.getString("DEVICE_BROWSER"))
                                .deviceType(rs.getString("DEVICE_TYPE"))
                                .isMobile((Boolean) rs.getObject("DEVICE_IS_MOBILE"))
                                .build();
                        GeoIpInfo geoIpInfo = GeoIpInfo.builder()
                                .ip(rs.getString("IP_ADDRESS"))
                                .city(rs.getString("CITY"))
                                .postalCode(rs.getString("POSTAL_CODE"))
                                .country(rs.getString("COUNTRY"))
                                .countryIsoCode(rs.getString("COUNTRY_ISO_CODE"))
                                .continent(rs.getString("CONTINENT"))
                                .latitude((Double) rs.getObject("LATITUDE"))
                                .longitude((Double) rs.getObject("LONGITUDE"))
                                .accuracyRadius((Integer) rs.getObject("ACCURACY_RADIUS"))
                                .build();

                        LoginRecordEntity entity = LoginRecordEntity.builder()
                                .id(rs.getString("ID"))
                                .userId(rs.getString("USER_ID"))
                                .time(time)
                                .device(device)
                                .geoIpInfo(geoIpInfo)
                                .build();
                        results.add(entity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch login history for userId=" + userId, e);
        }
        return results;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
