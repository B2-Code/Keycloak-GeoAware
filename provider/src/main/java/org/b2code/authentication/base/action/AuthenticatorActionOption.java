package org.b2code.authentication.base.action;

import lombok.RequiredArgsConstructor;
import org.b2code.authentication.device.action.SendDeviceWarningEmailAction;
import org.b2code.authentication.unknownip.action.SendIpWarningEmailAction;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Available authenticator actions as enum.
 * Each enum value represents a specific authentication action that can be performed.
 */
@RequiredArgsConstructor
public enum AuthenticatorActionOption {

    SEND_IP_WARNING_EMAIL(SendIpWarningEmailAction::instance),
    SEND_DEVICE_WARNING_EMAIL(SendDeviceWarningEmailAction::instance),
    DENY_ACCESS(DenyAccessAction::instance),
    LOG(LogAction::instance),
    DISABLE_USER(DisableUserAction::instance);

    private static final Map<String, AuthenticatorAction> ACTION_BY_LABEL = getActionByLabelMap();
    private final Supplier<AuthenticatorAction> actionSupplier;

    /**
     * Returns the authenticator action instance associated with this enum value.
     *
     * @return The {@link AuthenticatorAction} implementation for this option
     */
    public AuthenticatorAction getAction() {
        return actionSupplier.get();
    }

    /**
     * Finds an action by its label.
     * Looks up the action in a pre-populated map of actions indexed by their labels.
     *
     * @param label The label to search for
     * @return The corresponding action
     * @throws IllegalArgumentException if no matching action was found
     */
    public static AuthenticatorAction getActionByLabel(String label) {
        AuthenticatorAction action = ACTION_BY_LABEL.get(label);
        if (action == null) {
            throw new IllegalArgumentException("Unknown action: " + label);
        }
        return action;
    }

    /**
     * Creates a map of all available actions indexed by their labels.
     * This is used internally to initialize the {@link AuthenticatorActionOption#ACTION_BY_LABEL} constant.
     *
     * @return A map where keys are action labels and values are the corresponding actions
     */
    private static Map<String, AuthenticatorAction> getActionByLabelMap() {
        return Arrays.stream(values()).collect(Collectors.toMap(option -> option.getAction().getLabel(), AuthenticatorActionOption::getAction));
    }

}