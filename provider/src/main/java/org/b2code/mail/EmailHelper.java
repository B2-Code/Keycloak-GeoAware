package org.b2code.mail;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.keycloak.common.util.Time;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.locale.LocaleSelectorProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;
import org.keycloak.services.Urls;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JBossLog
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailHelper {

    public static void sendNewIpEmail(@NotNull GeoIpInfo geoIpInfo, @NotNull DeviceRepresentation deviceRepresentation, @NotNull KeycloakSession session, @NotNull UserModel user, @NotNull RealmModel realm) {
        Map<String, Object> params = new HashMap<>();
        params.putAll(getGeoIpParams(geoIpInfo));
        params.putAll(getUserAgentParams(deviceRepresentation));
        params.putAll(getResetCredentialText(session));
        send(session, user, realm, EmailType.LOGIN_FROM_NEW_IP, params);
    }

    public static void sendNewDeviceEmail(@NotNull GeoIpInfo geoIpInfo, @NotNull DeviceRepresentation deviceRepresentation, @NotNull KeycloakSession session, @NotNull UserModel user, @NotNull RealmModel realm) {
        Map<String, Object> params = new HashMap<>();
        params.putAll(getGeoIpParams(geoIpInfo));
        params.putAll(getUserAgentParams(deviceRepresentation));
        params.putAll(getResetCredentialText(session));
        send(session, user, realm, EmailType.LOGIN_FROM_NEW_DEVICE, params);
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

    private static Map<String, Object> getResetCredentialText(KeycloakSession session) {
        return Map.of("resetCredentialsHelp", session.getContext().getRealm().isResetPasswordAllowed() ? "${resetCredentialsActionHelpText} " + getResetCredentialUrl(session) : "${contactAdminHelpText}");
    }

    private static String getResetCredentialUrl(KeycloakSession session) {
        return Urls.loginResetCredentials(session.getContext().getUri().getBaseUri(), session.getContext().getRealm().getName()).toString();
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

    public static void send(KeycloakSession session, UserModel user, RealmModel realm, EmailType type, Map<String, Object> params) {
        log.debugf("Sending email to %s (%s)", user.getEmail(), type);
        AuthenticationSessionModel authenticatedSession = session.getContext().getAuthenticationSession();
        // Keycloak allows the language to be set in the login flow. Since an attacker could change the language, we need to ensure that the email is sent in the user's preferred or default language.
        String userRequestLocale = authenticatedSession.getAuthNote(LocaleSelectorProvider.USER_REQUEST_LOCALE);
        if (userRequestLocale != null) {
            authenticatedSession.removeAuthNote(LocaleSelectorProvider.USER_REQUEST_LOCALE);
        }
        try {
            EmailTemplateProvider emailTemplateProvider = session.getProvider(EmailTemplateProvider.class).setRealm(realm).setUser(user).setAuthenticationSession(authenticatedSession);
            emailTemplateProvider.send(type.getSubjectKey(), Collections.emptyList(), type.getTemplateName(), params);
        } catch (EmailException e) {
            log.error("Failed to send email. Please check if your email settings are correct.", e);
        }
        if (userRequestLocale != null) {
            authenticatedSession.setAuthNote(LocaleSelectorProvider.USER_REQUEST_LOCALE, userRequestLocale);
        }
    }

}
