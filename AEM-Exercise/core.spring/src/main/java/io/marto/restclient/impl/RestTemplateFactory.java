/*
 * Copyright 2017 - Martin Petrovsky - All rights reserved.
 */
package io.marto.restclient.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * Creates and configures {@link RestTemplate}s.
 */
public class RestTemplateFactory {

    private final ClientHttpRequestFactory httpRequestFactory;

    public RestTemplateFactory(ClientHttpRequestFactory httpRequestFactory) {
        this.httpRequestFactory = httpRequestFactory;
    }

    public RestTemplate createJsonRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

        SubTreeMappingJackson2HttpMessageConverter convertor = new SubTreeMappingJackson2HttpMessageConverter("query", "results", "channel", "item");
        convertor.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        messageConverters.add(convertor);
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

}
