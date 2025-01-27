package org.b2code.authentication.base.action;

import org.b2code.authentication.device.action.SendDeviceWarningEmailAction;
import org.b2code.authentication.unknownip.action.SendIpWarningEmailAction;

import java.util.stream.Stream;

public enum AuthenticatorActionOption {

    SEND_IP_WARNING_EMAIL {
        @Override
        public AuthenticatorAction getAction() {
            return SendIpWarningEmailAction.instance();
        }
    },
    SEND_DEVICE_WARNING_EMAIL {
        @Override
        public AuthenticatorAction getAction() {
            return SendDeviceWarningEmailAction.instance();
        }
    },
    DENY_ACCESS {
        @Override
        public AuthenticatorAction getAction() {
            return DenyAccessAction.instance();
        }
    },
    LOG {
        @Override
        public AuthenticatorAction getAction() {
            return LogAction.instance();
        }
    },
    DISABLE_USER {
        @Override
        public AuthenticatorAction getAction() {
            return DisableUserAction.instance();
        }
    };

    public abstract AuthenticatorAction getAction();

    public static AuthenticatorAction getActionByLabel(String label) {
        return Stream.of(values())
                .map(AuthenticatorActionOption::getAction)
                .filter(action -> action.getLabel().equals(label))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown action: " + label));
    }

}
