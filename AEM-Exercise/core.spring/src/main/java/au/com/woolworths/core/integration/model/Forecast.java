/*
 * Copyright 2017 - Martin Petrovsky - All rights reserved.
 */
package au.com.woolworths.core.integration.model;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.split;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import au.com.woolworths.core.integration.impl.LocalDateDeserializer;
import au.com.woolworths.core.integration.impl.LocalDateSeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Models a Weather Forecast as retrieved from Yahoo Weather API and/or stored as Sling Model {@link Resource}
 */
@Model(adaptables = Resource.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@EqualsAndHashCode(of = "date")
@ToString(exclude = "resource")
public class Forecast {

    /**
     * The date of the forecast
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSeserializer.class)
    @Getter @Setter
    private LocalDate date;

    /**
     * The high temperature
     */
    @Inject
    @Getter @Setter
    private double high;

    /**
     * The low temperature
     */
    @Inject
    @Getter @Setter
    private double low;

    /**
     * A short description - i.e. 'Mostly cloudy'
     */
    @JsonProperty("text")
    @Inject @Named("jcr:description")
    @Getter @Setter
    private String description;

    @Self
    private Resource resource;

    @Inject
    @Getter @Setter
    @JsonIgnore
    private Calendar asOf;

//    @Inject
//    public Forecast(@Self Resource resource, double high, double low, @Named("jcr:description") String description) {
//        this(extractDateFromPath(resource.getPath()), high, low, description);
//    }

    @PostConstruct
    public void setusp() {
        this.date = extractDateFromPath(resource.getPath());
    }

    private static final LocalDate extractDateFromPath(String path) {
        String[] split = split(path, "/");
        if (split.length < 3) {
            final String msg = dateParseError(path);
            LOG.info(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            return LocalDate.parse(format("%s-%s-%s", split[split.length - 3], split[split.length - 2], split[split.length - 1]), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            final String msg = dateParseError(path);
            LOG.info(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private static String dateParseError(String path) {
        return format("Invalid path('%s') for %s: Path does not denote a valid date", path, Forecast.class.getSimpleName());
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-M-d");
    private static final Logger LOG = LoggerFactory.getLogger(Forecast.class);

}
