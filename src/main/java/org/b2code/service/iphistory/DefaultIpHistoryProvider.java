package org.b2code.service.iphistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.authentication.UnknownIPAuthenticatorConfigProperties;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.b2code.service.mail.EmailHelper;
import org.b2code.service.useragent.UserAgentInfo;
import org.b2code.service.useragent.UserAgentParserProvider;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;

import java.util.ArrayList;
import java.util.List;

@JBossLog
public class DefaultIpHistoryProvider implements IpHistoryProvider {

    private static final String USER_ATTRIBUTE_LAST_IPS = "ip_history";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CollectionType recordListType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, LastIpRecord.class);

    private final KeycloakSession session;
    private final UserProvider userProvider;

    private final int retentionTimeHours;

    public DefaultIpHistoryProvider(KeycloakSession session, int retentionTimeHours) {
        log.tracef("Creating new %s", DefaultIpHistoryProvider.class.getSimpleName());
        this.session = session;
        this.retentionTimeHours = retentionTimeHours;
        this.userProvider = session.getProvider(UserProvider.class);
    }

    /**
     * Persists the IP address of a user in the history.
     *
     * @param ip     the IP address
     * @param userId the user ID
     */
    public void track(@NotNull String ip, @NotNull String userId, @NotNull String emailModus) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = userProvider.getUserById(realm, userId);

        List<LastIpRecord> lastIps = getLastIps(user);
        boolean knownIp = updateRecords(lastIps, ip);
        setLastIps(user, lastIps);

        if (!knownIp && emailModus.equals(UnknownIPAuthenticatorConfigProperties.UNKNOWN_IP)) {
            log.debugf("New IP for user %s (%s), sending notification mail", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else if (emailModus.equals(UnknownIPAuthenticatorConfigProperties.ALWAYS)) {
            log.debugf("Sending always notification mail for user %s (%s)", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        }
    }

    /**
     * Updates the list of IP records by removing old records and adding the new IP.
     *
     * @param lastIps the list of last IP records
     * @param ip      the new IP address to be added
     * @return true if the IP address was already known, false otherwise
     */
    private boolean updateRecords(List<LastIpRecord> lastIps, String ip) {
        long now = Time.currentTimeMillis();
        lastIps.removeIf(r -> now - r.getTime() > (long) retentionTimeHours * 60 * 60 * 1000);
        boolean knownIp = lastIps.removeIf(r -> r.getIp().equals(ip));
        lastIps.add(LastIpRecord.builder().ip(ip).time(Time.currentTimeMillis()).build());
        return knownIp;
    }

    private List<LastIpRecord> getLastIps(UserModel user) {
        String lastIpsJson = user.getFirstAttribute(USER_ATTRIBUTE_LAST_IPS);
        if (lastIpsJson != null) {
            log.debugf("Parsing user attribute %s = %s", USER_ATTRIBUTE_LAST_IPS, lastIpsJson);
            try {
                return objectMapper.readValue(lastIpsJson, recordListType);
            } catch (JsonProcessingException e) {
                log.errorf("Failed to parse last IPs: %s", e.getMessage());
            }
        } else {
            log.debugf("No user attribute %s", USER_ATTRIBUTE_LAST_IPS);
        }
        return new ArrayList<>();
    }

    private void setLastIps(UserModel user, List<LastIpRecord> lastIps) {
        try {
            String newValue = objectMapper.writeValueAsString(lastIps);
            log.debugf("Writing user attribute %s = %s", USER_ATTRIBUTE_LAST_IPS, newValue);
            user.setSingleAttribute(USER_ATTRIBUTE_LAST_IPS, newValue);
        } catch (JsonProcessingException e) {
            log.errorf("Failed to write last IPs: %s", e.getMessage());
        }
    }

    /**
     * Sends an email notification to the user about login and the IP address.
     *
     * @param user the user
     * @param ip   the new IP address
     */
    private void sentEmailNotification(UserModel user, String ip) {
        GeoipDatabaseAccessProvider geoipDatabaseAccessProvider = session.getProvider(GeoipDatabaseAccessProvider.class);
        UserAgentParserProvider userAgentParserProvider = session.getProvider(UserAgentParserProvider.class);
        GeoIpInfo geoIpInfo = geoipDatabaseAccessProvider.getIpInfo(ip);
        UserAgentInfo userAgentInfo = userAgentParserProvider.parse(session.getContext().getHttpRequest().getHttpHeaders().getHeaderString(HttpHeaders.USER_AGENT));
        EmailHelper.sendNewIpEmail(geoIpInfo, userAgentInfo, session, user, session.getContext().getRealm());
    }

    @Override
    public void close() {
        // NOOP
    }
}
