package org.b2code.config;

import org.b2code.authentication.device.DeviceAuthenticatorFactory;
import org.b2code.authentication.device.OnDeviceChangeConditionalAuthenticatorFactory;
import org.b2code.authentication.device.UnknownDeviceConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.IpAuthenticatorFactory;
import org.b2code.authentication.ip.OnIpChangeConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.UnknownIpConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.UnknownLocationConditionalAuthenticatorFactory;
import org.b2code.loginhistory.LoginTrackerEventListenerProviderFactory;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.testframework.realm.AuthenticationFlowConfigBuilder;
import org.keycloak.testframework.realm.RealmConfig;
import org.keycloak.testframework.realm.RealmConfigBuilder;

import java.util.List;

public class TestRealmConfig implements RealmConfig {

    public static final String AUTHENTICATOR_FLOW_PREFIX = "test-flow-";
    public static final String CONDITIONAL_FLOW_PREFIX = "test-conditional-flow-";
    public static final String CONDITIONAL_SUB_FLOW_PREFIX = "sub-flow-";

    private static final List<String> AUTHENTICATOR_PROVIDERS = List.of(
            IpAuthenticatorFactory.PROVIDER_ID,
            DeviceAuthenticatorFactory.PROVIDER_ID
    );

    private static final List<String> CONDITIONAL_PROVIDERS = List.of(
            OnIpChangeConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownIpConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownLocationConditionalAuthenticatorFactory.PROVIDER_ID,
            OnDeviceChangeConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownDeviceConditionalAuthenticatorFactory.PROVIDER_ID
    );

    @Override
    public RealmConfigBuilder configure(RealmConfigBuilder builder) {
        builder.name("test-realm")
                .eventsListeners(LoginTrackerEventListenerProviderFactory.ID)
                .displayName("Test Realm");

        for (String provider : AUTHENTICATOR_PROVIDERS) {
            AuthenticationFlowConfigBuilder flow = builder.addAuthenticationFlow(
                    AUTHENTICATOR_FLOW_PREFIX + provider, "", "basic-flow", true, false);
            flow.addAuthenticationExecutionWithAuthenticator(
                    UsernamePasswordFormFactory.PROVIDER_ID, "REQUIRED", 10, false);
            flow.addAuthenticationExecutionWithAuthenticator(provider, "REQUIRED", 20, false);
        }

        for (String provider : CONDITIONAL_PROVIDERS) {
            String subAlias = CONDITIONAL_SUB_FLOW_PREFIX + provider;

            AuthenticationFlowConfigBuilder subFlow = builder.addAuthenticationFlow(
                    subAlias, "", "basic-flow", false, false);
            subFlow.addAuthenticationExecutionWithAuthenticator(provider, "CONDITIONAL", 10, false);
            subFlow.addAuthenticationExecutionWithAuthenticator("deny-access-authenticator", "REQUIRED", 20, false);

            AuthenticationFlowConfigBuilder mainFlow = builder.addAuthenticationFlow(
                    CONDITIONAL_FLOW_PREFIX + provider, "", "basic-flow", true, false);
            mainFlow.addAuthenticationExecutionWithAuthenticator(
                    UsernamePasswordFormFactory.PROVIDER_ID, "REQUIRED", 10, false);
            mainFlow.addAuthenticationExecutionWithAliasFlow(subAlias, "CONDITIONAL", 20, false);
        }

        return builder;
    }
}
