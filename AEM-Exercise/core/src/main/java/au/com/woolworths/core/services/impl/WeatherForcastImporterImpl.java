package au.com.woolworths.core.services.impl;

import static java.lang.String.format;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import au.com.woolworths.core.integration.WeatherForcast;
import au.com.woolworths.core.integration.WeatherForcastService;
import au.com.woolworths.core.integration.model.Forecast;
import au.com.woolworths.core.services.WeatherForcastImporter;
import au.com.woolworths.core.utils.SlingResoureBuilder;

@Service
@Component
public class WeatherForcastImporterImpl implements WeatherForcastImporter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private WeatherForcastService forecaster;

    @Override
    public void importForecast() {
        try {
            importForecast(forecaster.getForecast("Sydney", "AU"));
        } catch (RestClientException e) {
            logger.error("Failed to fetch weather forecast", e);
            throw e;
        }
    }

    private void importForecast(WeatherForcast weatherForecast) {
        try {
            ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);
            try {
                final Resource base = getOrCreateBaseResource(resolver);
                for (Forecast forecast : weatherForecast.getForecast()) {
                    final Resource yearResource = getOrCreateYearResource(resolver, base, forecast);
                    final Resource monthResource = getOrCreateMonthResource(resolver, forecast, yearResource);
                    createOrUpdateForecast(resolver, monthResource, forecast, weatherForecast.getPubDate());
                }
            } catch (PersistenceException e) {
                throw new RuntimeException("Failed to import Weather Forecast Data", e);
            } finally {
                resolver.close();
            }
        } catch (LoginException e) {
            throw new RuntimeException("Failed to access JCR", e);
        }
    }

    private Resource getOrCreateBaseResource(ResourceResolver resolver) throws PersistenceException {
        Resource base = resolver.resolve("/content/weather");
        if (base instanceof NonExistingResource) {
            base = SlingResoureBuilder.addResource(resolver, "/content", "weather")
                .withJcrPrimaryType(JcrConstants.NT_UNSTRUCTURED)
                .persist();
            resolver.commit();
        }
        return base;
    }

    private void createOrUpdateForecast(ResourceResolver resolver, final Resource parent, Forecast forecast, ZonedDateTime publicationDate) throws PersistenceException {
        String dayOfMonth = format("%02d", forecast.getDate().getDayOfMonth());
        Resource forecastResource = parent.getChild(dayOfMonth);
        final SlingResoureBuilder builder;
        Forecast oldVersion = null;
        if (forecastResource != null) {
            builder = SlingResoureBuilder.modifyResource(forecastResource);
            oldVersion = forecastResource.adaptTo(Forecast.class);
        } else {
            builder = SlingResoureBuilder.addResource(resolver, parent, dayOfMonth);
        }

        // Only modified if the data in the repository does not exist or is stale according to the publicationDate
        if (oldVersion == null || oldVersion.getAsOf().before(GregorianCalendar.from(publicationDate))) {
            forecastResource = persistForecast(builder, forecast, publicationDate);
            resolver.commit();
            logger.debug("Persisted: {} to {}", forecast.toString(), forecastResource.getPath());
        } else {
            logger.debug("Not need to persist {} to {} as it's already there", forecast.toString(), forecastResource.getPath());
        }
    }

    private Resource persistForecast(final SlingResoureBuilder builder, Forecast forecast, ZonedDateTime publicationDate) throws PersistenceException {
        return builder.withJcrPrimaryType(JcrConstants.NT_UNSTRUCTURED)
            .withProperty("high", forecast.getHigh())
            .withProperty("low", forecast.getLow())
            .withProperty("jcr:description", forecast.getDescription())
            .withProperty("asOf", publicationDate)
            .persist();
    }

    private Resource getOrCreateYearResource(ResourceResolver resolver, Resource base, Forecast forecast) throws PersistenceException {
        final String year = String.valueOf(forecast.getDate().getYear());
        Resource yearResource = base.getChild(year);
        if (yearResource == null) {
            yearResource = SlingResoureBuilder.addResource(resolver, base, year)
                .withJcrPrimaryType(JcrConstants.NT_UNSTRUCTURED)
                .persist();
            resolver.commit();
        }
        return yearResource;
    }

    private Resource getOrCreateMonthResource(ResourceResolver resolver, Forecast forecast, Resource year) throws PersistenceException {
        final String month = format("%02d", forecast.getDate().getMonthValue());
        Resource monthResource = year.getChild(month);
        if (monthResource == null) {
            monthResource = SlingResoureBuilder.addResource(resolver, year, month)
                .withJcrPrimaryType(JcrConstants.NT_UNSTRUCTURED)
                .persist();
            resolver.commit();
        }
        return monthResource;
    }
}
