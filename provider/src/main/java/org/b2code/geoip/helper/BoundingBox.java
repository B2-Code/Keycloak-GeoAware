package org.b2code.geoip.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoundingBox {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
}
