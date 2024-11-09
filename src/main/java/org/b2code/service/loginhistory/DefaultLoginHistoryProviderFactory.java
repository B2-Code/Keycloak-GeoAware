package org.b2code.service.loginhistory;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.common.util.Environment;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPAttributePermissions;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.keycloak.representations.userprofile.config.UPGroup;
import org.keycloak.userprofile.UserProfileConstants;
import org.keycloak.userprofile.UserProfileProvider;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@JBossLog
@AutoService(LoginHistoryProviderFactory.class)
public class DefaultLoginHistoryProviderFactory implements LoginHistoryProviderFactory {

    private static final String RECORD_RETENTION_TIME_PARAM = "recordRetentionTime";
    private static final String RECORD_RETENTION_TIME_DEFAULT = "P1D";

    private static final String MAX_RECORDS_PARAM = "recordMax";
    private static final int MAX_RECORDS_DEFAULT = 100;

    private static final String SHOW_HISTORY_IN_ACCOUNT_PARAM = "showHistoryInAccount";
    private static final boolean SHOW_HISTORY_IN_ACCOUNT_DEFAULT = Environment.isDevMode();

    private Config.Scope config;

    @Override
    public DefaultLoginHistoryProvider create(KeycloakSession session) {
        String retentionTimeValue = config.get(RECORD_RETENTION_TIME_PARAM, RECORD_RETENTION_TIME_DEFAULT);
        Duration retentionTime;
        try {
            retentionTime = Duration.parse(retentionTimeValue);
            if (retentionTime.isNegative() || retentionTime.isZero()) {
                throw new DateTimeParseException("Retention time must be positive", retentionTimeValue, 0);
            }
        } catch (DateTimeParseException e) {
            log.errorf("Invalid retention time format: '%s'. Please use ISO-8601 duration format. Using default value.", retentionTimeValue);
            retentionTime = Duration.parse(RECORD_RETENTION_TIME_DEFAULT);
        }

        int maxRecords = config.getInt(MAX_RECORDS_PARAM, MAX_RECORDS_DEFAULT);
        return new DefaultLoginHistoryProvider(session, retentionTime, maxRecords);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        keycloakSessionFactory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent postCreateEvent) {
                if (postCreateEvent.getKeycloakSession().getContext().getRealm() == null) {
                    // For some reason the realm context is set for creations, but null for imports
                    postCreateEvent.getKeycloakSession().getContext().setRealm(postCreateEvent.getCreatedRealm());
                    this.updateUserProfile(postCreateEvent.getKeycloakSession());
                    postCreateEvent.getKeycloakSession().getContext().setRealm(null);
                } else {
                    this.updateUserProfile(postCreateEvent.getKeycloakSession());
                }
            } else if (event instanceof PostMigrationEvent) {
                KeycloakModelUtils.runJobInTransaction(keycloakSessionFactory, session -> session.getProvider(RealmProvider.class).getRealmsStream().forEach(realm -> {
                    session.getContext().setRealm(realm);
                    this.updateUserProfile(session);
                }));
            }
        });
    }

    private void updateUserProfile(KeycloakSession session) {
        UserProfileProvider userProfileProvider = session.getProvider(UserProfileProvider.class);
        UPConfig existingUpConfig = userProfileProvider.getConfiguration().clone();

        String groupName = "login-history";
        UPGroup expectedGroup = new UPGroup(groupName);
        expectedGroup.setDisplayHeader("${loginHistoryUserProfileAttributeGroup}");
        expectedGroup.setDisplayDescription("${loginHistoryUserProfileAttributeGroupDescription}");

        List<UPGroup> existingGroups = existingUpConfig.getGroups().stream()
                .filter(group -> group.getName().equals(expectedGroup.getName()))
                .toList();

        if (existingGroups.size() != 1) {
            if (existingGroups.size() > 1) {
                log.warnf("Multiple user profile groups with name '%s' found in realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
                List<UPGroup> groups = existingUpConfig.getGroups();
                groups.removeIf(group -> group.getName().equals(expectedGroup.getName()));
                existingUpConfig.setGroups(groups);
            }
            log.debugf("Adding user profile group '%s' to realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
            UPConfig newUpConfig = existingUpConfig.addGroup(expectedGroup);
            userProfileProvider.setConfiguration(newUpConfig);
        } else {
            UPGroup existingGroup = existingGroups.getFirst();
            if (!existingGroup.equals(expectedGroup)) {
                log.debugf("Updating user profile group '%s' in realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
                existingUpConfig.getGroups().removeIf(group -> group.getName().equals(existingGroup.getName()));
                UPConfig newUpConfig = existingUpConfig.addGroup(expectedGroup);
                userProfileProvider.setConfiguration(newUpConfig);
            }
        }

        boolean showAttribute = config.getBoolean(SHOW_HISTORY_IN_ACCOUNT_PARAM, SHOW_HISTORY_IN_ACCOUNT_DEFAULT);
        Set<String> viewPermissions = showAttribute ? Set.of(UserProfileConstants.ROLE_ADMIN) : Collections.emptySet();
        Set<String> editPermissions = showAttribute && Environment.isDevMode() ? Set.of(UserProfileConstants.ROLE_ADMIN) : Collections.emptySet();
        UPAttribute expectedAttribute = new UPAttribute(DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS, true, new UPAttributePermissions(viewPermissions, editPermissions));
        expectedAttribute.setDisplayName("${loginHistoryUserProfileAttribute}");
        expectedAttribute.setGroup(groupName);
        UPAttribute existingAttribute = existingUpConfig.getAttribute(DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS);
        if (existingAttribute == null || !existingAttribute.equals(expectedAttribute)) {
            log.debugf("Updating user profile attribute '%s' in realm '%s'", DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS, session.getContext().getRealm().getName());
            UPConfig newUpConfig = existingUpConfig.addOrReplaceAttribute(expectedAttribute);
            userProfileProvider.setConfiguration(newUpConfig);
        }
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return "default";
    }
}
