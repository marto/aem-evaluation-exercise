/*
 * Copyright 2017 - Martin Petrovsky - All rights reserved.
 */
package io.marto.restclient.impl;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.marto.restclient.RestClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link RestClient} that uses a {@link RestTemplate}.
 *
 * @param <T> see {@link RestClient}
 */
@RequiredArgsConstructor
@Getter
public class SimpleRestClient<T> implements RestClient<T> {
    private final RestTemplate restTemplate;
    private final String url;
    private final Logger logger;

    public SimpleRestClient(RestTemplate restTemplate, String url, Class<?> logger) {
        this(restTemplate, url, LoggerFactory.getLogger(logger));
    }

    private T callRestTemplate(Class<T> returnType, Map<String, ?> urlVariables) {
        logger.debug("Calling {}", url);
        final StopWatch sw = new StopWatch();
        sw.start();
        try {
            T response = restTemplate.getForObject(url, returnType, urlVariables);
            logger.debug("Returning {}", response);
            return response;
        } catch (HttpMessageConversionException e) {
            throw new RestClientException(e.getMessage(), e);
        } finally {
            sw.stop();
            logger.info("Call to url={} with=[{}] took={}", new Object[] { url, join(urlVariables, ","), sw});
        }
    }

    @Override
    public T get(Class<T> returnType, Map<String, ?> args) throws RestClientException {
        return callRestTemplate(returnType, args);
    }

}
