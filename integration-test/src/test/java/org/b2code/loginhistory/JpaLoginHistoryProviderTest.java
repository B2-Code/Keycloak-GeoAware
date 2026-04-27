package org.b2code.loginhistory;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.b2code.base.BaseTest;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.util.ApiUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Map;
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
    void testLoginHistoryIsDeletedOnRealmDeletion() throws Exception {
        // Use a throwaway realm so the framework-managed realm stays intact.
        // Deleting the framework realm causes OAuthClient.close() to fail in afterEach
        // (the re-created realm lacks the test OAuth client), aborting the destroy loop
        // and cascading 409s to every subsequent test's beforeEach.
        String tempRealmName = "temp-realm-" + UUID.randomUUID();
        RealmRepresentation tempRealmRep = new RealmRepresentation();
        tempRealmRep.setRealm(tempRealmName);
        tempRealmRep.setEnabled(true);
        adminClient.realms().create(tempRealmRep);

        // Create a real user in the temp realm — required because deleteByRealmId
        // queries UserEntity.realmId to find which records to delete.
        String tempUserId;
        UserRepresentation tempUser = new UserRepresentation();
        tempUser.setUsername("temp-user-" + UUID.randomUUID());
        tempUser.setEnabled(true);
        try (Response resp = adminClient.realm(tempRealmName).users().create(tempUser)) {
            Assertions.assertEquals(201, resp.getStatus(), "Temp user must be created successfully");
            tempUserId = ApiUtil.getCreatedId(resp);
        }

        // Insert a login record directly — the plugin uses event listeners for deletion,
        // so a directly-inserted record still exercises the cascade correctly.
        Map<String, String> dbConfig = testDatabase.serverConfig();
        try (Connection conn = DriverManager.getConnection(
                dbConfig.get("db-url"), dbConfig.get("db-username"), dbConfig.get("db-password"));
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO geoaware_login_record (ID, USER_ID, TIMESTAMP, IP_ADDRESS) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, tempUserId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "127.0.0.1");
            ps.executeUpdate();
        }

        Assertions.assertEquals(1, loginHistory.getAllByUserId(tempUserId).size(),
                "Login record must exist before realm deletion");

        adminClient.realm(tempRealmName).remove();

        // Wait for deletion — Keycloak 26.6+ deletes asynchronously.
        long deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                adminClient.realm(tempRealmName).toRepresentation();
            } catch (NotFoundException e) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Assertions.assertEquals(0, loginHistory.getAllByUserId(tempUserId).size(),
                "Login history records must be deleted after realm deletion");
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
