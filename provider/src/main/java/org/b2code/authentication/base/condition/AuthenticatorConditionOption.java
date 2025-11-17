package org.b2code.authentication.base.condition;

import org.b2code.authentication.device.condition.StrictOnDeviceChangeCondition;
import org.b2code.authentication.device.condition.StrictOnUnknownDeviceCondition;
import org.b2code.authentication.ip.condition.OnIpChangeCondition;
import org.b2code.authentication.ip.condition.UnknownIpCondition;
import org.b2code.authentication.ip.condition.UnknownLocationCondition;

import java.util.stream.Stream;

public enum AuthenticatorConditionOption {

    ALWAYS {
        @Override
        public AuthenticatorCondition getCondition() {
            return AlwaysCondition.instance();
        }
    },
    NEVER {
        @Override
        public AuthenticatorCondition getCondition() {
            return NeverCondition.instance();
        }
    },
    ON_IP_CHANGE {
        @Override
        public AuthenticatorCondition getCondition() {
            return OnIpChangeCondition.instance();
        }
    },
    UNKNOWN_LOCATION {
        @Override
        public AuthenticatorCondition getCondition() {
            return UnknownLocationCondition.instance();
        }
    },
    UNKNOWN_IP {
        @Override
        public AuthenticatorCondition getCondition() {
            return UnknownIpCondition.instance();
        }
    },
    STRICT_ON_DEVICE_CHANGE {
        @Override
        public AuthenticatorCondition getCondition() {
            return StrictOnDeviceChangeCondition.instance();
        }
    },
    STRICT_ON_UNKNOWN_DEVICE {
        @Override
        public AuthenticatorCondition getCondition() {
            return StrictOnUnknownDeviceCondition.instance();
        }
    };

    public abstract AuthenticatorCondition getCondition();

    public static AuthenticatorCondition getConditionByLabel(String label) {
        return Stream.of(values())
                .map(AuthenticatorConditionOption::getCondition)
                .filter(condition -> condition.getLabel().equals(label))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown condition: " + label));
    }
}