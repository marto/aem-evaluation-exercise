package au.com.woolworths.core.integration;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.woolworths.core.integration.impl.ZonedDateTimeDeserializer;
import au.com.woolworths.core.integration.model.Forecast;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response object from Yahoo
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForcast {
    private String title;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime pubDate;
    private List<Forecast> forecast;
}