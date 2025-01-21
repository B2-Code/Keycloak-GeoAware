package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.DatabaseReader;

public class MaxmindFileProvider extends MaxmindProvider {

    public MaxmindFileProvider(DatabaseReader databaseReader) {
        super(databaseReader);
    }

}
