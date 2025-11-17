package org.b2code.loginhistory;

import jakarta.ws.rs.core.Response;
import org.b2code.base.BaseTest;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.util.ApiUtil;

import java.util.UUID;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
public class JpaLoginHistoryProviderTest extends BaseTest {

    @Test
    void testLoginHistoryIsDeletedOnAccountDeletion() {
        login();

        int loginRecordsBeforeDeletion = getLoginRecords().size();
        Assertions.assertEquals(1, loginRecordsBeforeDeletion, "Exactly one login history record is expected after initial login");

        user.admin().remove();

        int loginRecordsAfterDeletion = getLoginRecords().size();
        Assertions.assertEquals(0, loginRecordsAfterDeletion, "Login history records must be deleted after user account deletion");
    }

    @Test
    void testLoginHistoryIsDeletedOnRealmDeletion() {
        login();

        int loginRecordsBeforeDeletion = getLoginRecords().size();
        Assertions.assertEquals(1, loginRecordsBeforeDeletion, "Exactly one login history record is expected after initial login");

        RealmRepresentation realmRep = realm.getCreatedRepresentation();
        realm.admin().remove();

        int loginRecordsAfterDeletion = getLoginRecords().size();
        Assertions.assertEquals(0, loginRecordsAfterDeletion, "Login history records must be deleted after realm deletion");

        // Re-create the realm to not confuse the test framework
        adminClient.realms().create(realmRep);
    }

    @Test
    void testLoginHistoryIsNotDeletedWhenDifferentUserIsDeleted() {
        // Perform initial login which should create one login history record
        login();
        int initialRecords = getLoginRecords().size();
        Assertions.assertEquals(1, initialRecords, "Exactly one login history record is expected after initial login");

        // Create a different user
        String otherUsername = "other-user-" + UUID.randomUUID();
        UserRepresentation otherUser = new UserRepresentation();
        otherUser.setUsername(otherUsername);
        otherUser.setEnabled(true);

        try (Response createResp = realm.admin().users().create(otherUser)) {
            Assertions.assertEquals(201, createResp.getStatus(), "Different user should be created successfully");
            String otherUserId = ApiUtil.getCreatedId(createResp);
            Assertions.assertNotNull(otherUserId, "Created user id must not be null");

            // Delete the different user
            realm.admin().users().get(otherUserId).remove();
        }

        // Ensure login history for the original user remains untouched
        int afterDeletion = getLoginRecords().size();
        Assertions.assertEquals(initialRecords, afterDeletion, "Login history must remain unchanged when deleting a different user");
    }

    @Test
    void testLoginHistoryIsNotDeletedWhenDifferentRealmIsDeleted() {
        // Perform initial login which should create one login history record
        login();
        int initialRecords = getLoginRecords().size();
        Assertions.assertEquals(1, initialRecords, "Exactly one login history record is expected after initial login");

        // Create a different realm
        String otherRealmName = "other-realm-" + UUID.randomUUID();
        RealmRepresentation otherRealm = new RealmRepresentation();
        otherRealm.setRealm(otherRealmName);
        otherRealm.setEnabled(true);
        // In this test framework create does not return Response
        adminClient.realms().create(otherRealm);

        // Delete the different realm
        adminClient.realm(otherRealmName).remove();

        // Ensure login history for the original realm remains untouched
        int afterDeletion = getLoginRecords().size();
        Assertions.assertEquals(initialRecords, afterDeletion, "Login history must remain unchanged when deleting a different realm");
    }
}
