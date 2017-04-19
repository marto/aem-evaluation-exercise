/*
 * Copyright 2017 - Martin Petrovsky - All rights reserved.
 */
package io.marto.restclient;

import java.util.Map;

import org.springframework.web.client.RestClientException;

/**
 * An abstraction to call a remote rest end point. Use "Composition over Inheritance" to
 * chain and add different functionality to the RestClient, such as a circuit breaker,
 * performance monitoring of a REST end-point, caching or intelligent retries.
 *
 * @param <T> the type that is returned from the remote end-point
 */
public interface RestClient<T> {

    /**
     * GET request
     *
     * @param returnType the concrete type to return
     * @param args list of arguments to be passed
     *
     * @return the result of the REST call, or null if and only if <code>returnType</code> was set to null
     *
     * @throws RestClientException when things go wrong
     */
     T get(Class<T> returnType, Map<String, ?> args) throws RestClientException;
}
