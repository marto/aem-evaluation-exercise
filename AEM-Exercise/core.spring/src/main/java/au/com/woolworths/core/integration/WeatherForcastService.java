package au.com.woolworths.core.integration;

import au.com.woolworths.core.integration.model.Forecast;

/**
 * Service to retrieve Weather Forecasts
 */
public interface WeatherForcastService {

    /**
     * @param area Town or City (eg: "Sydney")
     * @param countryCode ISO Country Code (eg: "AU")
     *
     * @return a list of Weather {@link Forecast}s
     * @throws org.springframework.web.client.RestClientException on error
     */
    WeatherForcast getForecast(String area, String countryCode);
}
