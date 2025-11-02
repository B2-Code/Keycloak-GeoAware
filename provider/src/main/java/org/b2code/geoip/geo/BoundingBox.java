package org.b2code.geoip.geo;

import lombok.Value;

@Value
public class BoundingBox {
    double minLatitude;
    double maxLatitude;
    double minLongitude;
    double maxLongitude;

}
