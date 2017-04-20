/*
 * Copyright 2017 - Martin Petrovsky - All rights reserved.
 */
package au.com.woolworths.core.utils;

import static java.lang.String.format;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * A helper builder class to build/create {@link Resource}s
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SlingResoureBuilder {
    private final ResourceResolver resolver;
    private final Resource parent;
    private final String name;
    private final Resource resource;
    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    public static SlingResoureBuilder addResource(ResourceResolver resolver, String absolutePath, String name) throws PersistenceException {
        return addResource(resolver, resolver.resolve(absolutePath), name);
    }

    public static SlingResoureBuilder addResource(ResourceResolver resolver, Resource parent, String name) throws PersistenceException {
        if (parent instanceof NonExistingResource) {
            throw new PersistenceException(format("Could not create '%s' because parent('%s') could not be located", name, parent.toString()));
        }
        return new SlingResoureBuilder(resolver, parent, name, null);
    }

    public static SlingResoureBuilder modifyResource(Resource existing) {
        SlingResoureBuilder builder = new SlingResoureBuilder(existing.getResourceResolver(), existing.getParent(), existing.getName(), existing);
        builder.properties = existing.adaptTo(ModifiableValueMap.class);
        return builder;
    }

    public SlingResoureBuilder withJcrPrimaryType(String type) {
        return withProperty(JcrConstants.JCR_PRIMARYTYPE, type);
    }

    public SlingResoureBuilder withProperty(String name, Object value) {
        checkState();
        this.properties.put(name, value);
        return this;
    }

    public SlingResoureBuilder withProperty(String name, ZonedDateTime value) {
        checkState();
        this.properties.put(name, GregorianCalendar.from(value));
        return this;
    }

    /**
     * Persist the resource to the repository. The {@link SlingResoureBuilder} must no longer be used after calling this method;
     * @return
     * @throws PersistenceException
     */
    public Resource persist() throws PersistenceException {
        checkState();
        try {
            // when parent == null we are modifying not creating a new resource;
            if (this.resource == null) {
                return resolver.create(parent, name, properties);
            } else {
                return resource;
            }
        } finally {
            properties = null;
        }
    }

    private void checkState() {
        if (properties == null) {
            throw new IllegalArgumentException(format("%s.persist must have been already called to persist %s(%s=%s)", getClass().getSimpleName(), name, JcrConstants.JCR_PRIMARYTYPE, properties.get(JcrConstants.JCR_PRIMARYTYPE)));
        }
    }
}
