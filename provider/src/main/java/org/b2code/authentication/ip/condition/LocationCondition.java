package org.b2code.authentication.ip.condition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.authentication.ip.LocationConditionalAuthenticatorFactory;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.provider.GeoIpProvider;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Arrays;
import java.util.Map;

/**
 * Conditional authenticator that matches based on the geographic location (country or continent)
 * of the client's IP address. Used in Keycloak authentication flows to gate access by location.
 */
@JBossLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationCondition implements ConditionalAuthenticator {

    public static final String LABEL = "Location condition";

    public static final LocationCondition SINGLETON = new LocationCondition();


    @Override
    public boolean matchCondition(AuthenticationFlowContext  context) {

        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        var session = context.getSession();
        GeoIpProvider geoipProvider = session.getProvider(GeoIpProvider.class);
        String ip = session.getContext().getConnection().getRemoteAddr();

        String valueType = config.getOrDefault(LocationConditionalAuthenticatorFactory.CONFIG_VALUE_TYPE, LocationConditionalAuthenticatorFactory.COUNTRY_ISO_CODE);
        String values = config.get(LocationConditionalAuthenticatorFactory.CONFIG_VALUES);
        boolean reveseDecision = Boolean.parseBoolean(config.getOrDefault(LocationConditionalAuthenticatorFactory.CONFIG_REVERT,"false"));

        var value = "??";
        GeoIpInfo geoIpInfo = geoipProvider != null ? geoipProvider.getIpInfo(ip) : null;
        if (geoIpInfo != null) {
            value = switch (valueType) {
                case LocationConditionalAuthenticatorFactory.COUNTRY_ISO_CODE -> geoIpInfo.getCountryIsoCode() != null ? geoIpInfo.getCountryIsoCode() : "??";
                case LocationConditionalAuthenticatorFactory.COUNTRY -> geoIpInfo.getCountry() != null ? geoIpInfo.getCountry() : "??";
                case LocationConditionalAuthenticatorFactory.CONTINENT -> geoIpInfo.getContinent() != null ? geoIpInfo.getContinent() : "??";
                default -> throw new IllegalStateException("Unexpected value: " + valueType);
            };
        } else {
            log.warnf("Checking location condition for IP: %s, can't get ip info", ip);
        }
        var isMatch = Arrays.asList(values.split("##")).contains(value);
        log.debugf("Checking location condition for IP: %s, Value: %s in Values %s => %s", ip, value, values, isMatch);

        if (reveseDecision)
            return !isMatch;
        return isMatch;
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Not used
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {
        // Does nothing
    }

    public String getLabel() {
        return LABEL;
    }

    /** Returns a human-readable description of this condition for the Keycloak admin console. */
    public String getHelpText() {
        return "Check the location of the user's IP address against a list of allowed locations.";
    }

    /** Returns the singleton instance of this conditional authenticator. */
    public static LocationCondition instance() {
        return SINGLETON;
    }
}
