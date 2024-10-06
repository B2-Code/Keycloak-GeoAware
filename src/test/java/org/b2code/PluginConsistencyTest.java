package org.b2code;

import org.b2code.authentication.GeoIpFilterAuthenticatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginConsistencyTest {

    @Test
    void testProviderIdHasNotChanged() {
        assertEquals("geo-ip-filter", new GeoIpFilterAuthenticatorFactory().getId());
    }

    @Test
    void testDisplayTypeHasNotChanged() {
        assertEquals("Geo-IP-Filter", new GeoIpFilterAuthenticatorFactory().getDisplayType());
    }

}
