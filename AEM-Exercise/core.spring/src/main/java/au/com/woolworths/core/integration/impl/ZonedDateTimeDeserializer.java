package au.com.woolworths.core.integration.impl;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * {@link ZonedDateTime} Json deserializer with custom YQL format
 */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ZonedDateTime.parse(jp.getText(), DATE_FORMAT);
    }

    //Wed, 19 Apr 2017 08:00 AM AEST
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy hh:mm a zzz");
}