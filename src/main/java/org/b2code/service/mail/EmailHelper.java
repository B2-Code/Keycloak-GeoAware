package org.b2code.service.mail;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.common.util.Time;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.AbstractKeycloakTransaction;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JBossLog
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailHelper extends AbstractKeycloakTransaction {

    private KeycloakSession session;
    private UserModel user;
    private RealmModel realm;
    private EmailType type;
    private Map<String, Object> params;

    private static void sendAsyncEmail(KeycloakSession session, UserModel user, RealmModel realm, EmailType type, Map<String, Object> params) {
        EmailHelper transaction = new EmailHelper(session, user, realm, type, params);
        session.getTransactionManager().enlistAfterCompletion(transaction);
    }

    public static void sendNewIpEmail(@NotNull GeoIpInfo geoIpInfo, @NotNull DeviceRepresentation deviceRepresentation, @NotNull KeycloakSession session, @NotNull UserModel user, @NotNull RealmModel realm) {
        Map<String, Object> params = new HashMap<>();
        params.putAll(getGeoIpParams(geoIpInfo));
        params.putAll(getUserAgentParams(deviceRepresentation));
        sendAsyncEmail(session, user, realm, EmailType.LOGIN_FROM_NEW_IP, params);
    }

    public static void sendNewDeviceEmail(@NotNull GeoIpInfo geoIpInfo, @NotNull DeviceRepresentation deviceRepresentation, @NotNull KeycloakSession session, @NotNull UserModel user, @NotNull RealmModel realm) {
        Map<String, Object> params = new HashMap<>();
        params.putAll(getGeoIpParams(geoIpInfo));
        params.putAll(getUserAgentParams(deviceRepresentation));
        sendAsyncEmail(session, user, realm, EmailType.LOGIN_FROM_NEW_DEVICE, params);
    }

    private static Map<String, Object> getGeoIpParams(GeoIpInfo geoIpInfo) {
        if (geoIpInfo == null) {
            return Map.of("city", "?", "country", "?", "date", new Date(Time.currentTimeMillis()), "ip", "?");
        }
        Map<String, Object> geoIpParams = new HashMap<>();
        geoIpParams.put("city", geoIpInfo.getCity() != null ? geoIpInfo.getCity() : "?");
        geoIpParams.put("country", geoIpInfo.getCountry() != null ? geoIpInfo.getCountry() : "?");
        geoIpParams.put("date", new Date(Time.currentTimeMillis()));
        geoIpParams.put("ip", geoIpInfo.getIp());
        return geoIpParams;
    }

    private static Map<String, Object> getUserAgentParams(DeviceRepresentation userAgentInfo) {
        if (userAgentInfo == null) {
            return Map.of("browser", "?", "os", "?");
        }
        Map<String, Object> userAgentParams = new HashMap<>();
        userAgentParams.put("browser", userAgentInfo.getBrowser() != null ? userAgentInfo.getBrowser() : "?");
        String os = userAgentInfo.getOs();
        if (os != null && userAgentInfo.getOsVersion() != null) {
            os += " " + userAgentInfo.getOsVersion();
        }
        userAgentParams.put("os", os != null ? os : "?");
        return userAgentParams;
    }

    @Override
    public void commitImpl() {
        try {
            log.debugf("Sending email to %s (%s)", user.getEmail(), type);
            session.getProvider(EmailTemplateProvider.class).setRealm(realm).setUser(user).setAuthenticationSession(session.getContext().getAuthenticationSession()).send(type.getSubjectKey(), Collections.emptyList(), type.getTemplateName(), params);
        } catch (EmailException e) {
            log.error("Failed to send email. Please check if your email settings are correct.", e);
        }
    }

    @Override
    public void rollbackImpl() {
        // NOOP
    }
}
