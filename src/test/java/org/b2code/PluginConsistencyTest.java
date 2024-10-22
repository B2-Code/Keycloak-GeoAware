package org.b2code;

import org.b2code.authentication.UnknownIPAuthenticatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginConsistencyTest {

    @Test
    void testProviderIdHasNotChanged() {
        assertEquals("unknown-ip", new UnknownIPAuthenticatorFactory().getId());
    }

    @Test
    void testDisplayTypeHasNotChanged() {
        assertEquals("Unknown IP", new UnknownIPAuthenticatorFactory().getDisplayType());
    }

}
