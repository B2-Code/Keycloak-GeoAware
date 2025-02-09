package org.b2code.loginhistory;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.admin.PluginConfigWrapper;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

@JBossLog
@AutoService(LoginHistoryProviderFactory.class)
public class DefaultLoginHistoryProviderFactory implements LoginHistoryProviderFactory {

    private static final String USER_PROFILE_ATTRIBUTE_GROUP_NAME = "login-history";

    @Override
    public DefaultLoginHistoryProvider create(KeycloakSession session) {
        PluginConfigWrapper pluginConfig = PluginConfigWrapper.of(session);
        Duration retentionTime = Duration.ofDays(pluginConfig.getLoginHistoryRetentionDays());
        int maxRecords = pluginConfig.getLoginHistoryMaxRecords();
        return new DefaultLoginHistoryProvider(session, retentionTime, maxRecords);
    }

    @Override
    public void init(Config.Scope scope) {
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
        UPGroup expectedGroup = getExpectedUpGroup();

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

        UPAttribute expectedAttribute = getExpectedUpAttribute();
        UPAttribute existingAttribute = existingUpConfig.getAttribute(DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS);
        if (existingAttribute == null || !existingAttribute.equals(expectedAttribute)) {
            log.debugf("Updating user profile attribute '%s' in realm '%s'", DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS, session.getContext().getRealm().getName());
            UPConfig newUpConfig = existingUpConfig.addOrReplaceAttribute(expectedAttribute);
            userProfileProvider.setConfiguration(newUpConfig);
        }
    }

    private static UPAttribute getExpectedUpAttribute() {
        Set<String> viewPermissions = Set.of(UserProfileConstants.ROLE_ADMIN);
        Set<String> editPermissions = Environment.isDevMode() ? Set.of(UserProfileConstants.ROLE_ADMIN) : Collections.emptySet();
        UPAttribute expectedAttribute = new UPAttribute(DefaultLoginHistoryProvider.USER_ATTRIBUTE_LAST_IPS, true, new UPAttributePermissions(viewPermissions, editPermissions));
        expectedAttribute.setDisplayName("${loginHistoryUserProfileAttribute}");
        expectedAttribute.setGroup(USER_PROFILE_ATTRIBUTE_GROUP_NAME);
        return expectedAttribute;
    }

    private static UPGroup getExpectedUpGroup() {
        UPGroup expectedGroup = new UPGroup(USER_PROFILE_ATTRIBUTE_GROUP_NAME);
        expectedGroup.setDisplayHeader("${loginHistoryUserProfileAttributeGroup}");
        expectedGroup.setDisplayDescription("${loginHistoryUserProfileAttributeGroupDescription}");
        return expectedGroup;
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
