package org.b2code.authentication;

import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.config.TestRealmConfig;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.representations.idm.RealmRepresentation;

@Slf4j
abstract class BaseConditionalAuthenticatorProviderTest extends BaseTest {

    @BeforeEach
    void setupFlow() {
        RealmRepresentation realmRep = realm.getCreatedRepresentation();
        realmRep.setBrowserFlow(getFlowAlias());
        realm.admin().update(realmRep);
    }

    private String getFlowAlias() {
        return TestRealmConfig.CONDITIONAL_FLOW_PREFIX + getConditionalProviderToTest();
    }

    abstract String getConditionalProviderToTest();
}
