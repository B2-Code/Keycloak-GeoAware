package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.WebServiceClient;

public class MaxmindWebServiceProvider extends MaxmindProvider {

    public MaxmindWebServiceProvider(WebServiceClient webClient) {
        super(webClient);
    }

}
