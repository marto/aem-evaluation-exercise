package au.com.woolworths.core.servlets;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import javax.servlet.ServletException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.resourceresolver.MockHelper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class WeatherForecastServletTest {

    @Rule
    public final AemContext context = new AemContext();

    private ResourceResolver resourceResolver;

    private Clock clock = Clock.fixed(Instant.parse("2017-04-20T12:12:00.000Z"), ZoneId.of("UTC"));

    @InjectMocks
    private WeatherForecastServlet servlet = new WeatherForecastServlet();

    @Before
    public void setUp() throws Exception {
        this.resourceResolver = context.resourceResolver();
        servlet.setClock(clock);
        servlet.init();

        context.addModelsForPackage("au.com.woolworths.core.integration.model");

        MockHelper.create(resourceResolver).resource("/content").commit();
    }

    @Test
    public void testServiceReturnsNoDataWhenNoDataIsAvailable() throws IOException, ServletException, JSONException {
        givenNoWeatherData();

        whenServletIsCalled();

        thenServletReturned(forecasts("[]"));
    }

    @Test
    public void testServiceReturnsDataWhenDataIsAvailable() throws IOException, ServletException, JSONException {
        givenWeatherData();

        whenServletIsCalled();

        thenServletReturned(forecasts(
            "["
            +"   {"
            +"      \"text\" : \"Cloudy\","
            +"      \"high\" : 24,"
            +"      \"date\" : \"2017-04-21\","
            +"      \"low\" : 10"
            +"   },"
            +"   {"
            +"      \"date\" : \"2017-04-22\","
            +"      \"high\" : 33,"
            +"      \"text\" : \"Sunny\","
            +"      \"low\" : 10"
            +"   }"
            +"]"
        ));
    }

    private void givenWeatherData() throws PersistenceException {
        MockHelper.create(resourceResolver)
            .resource("/content/weather").resource("2017").resource("04").resource("21")
                .p("jcr:description", "Cloudy")
                .p("asOf", "2017-04-19T22:00:00.000+10:00")
                .p("low", "10")
                .p("high", "24")
            .resource("/content/weather/2017/04/22")
                .p("jcr:description", "Sunny")
                .p("asOf", "2017-04-19T22:00:00.000+10:00")
                .p("low", "10")
                .p("high", "33")
            .commit();
    }

    private void thenServletReturned(String forecasts) throws JSONException {
        JSONAssert.assertEquals(forecasts, context.response().getOutputAsString(), false);
    }

    private String forecasts(String forecasts) {
        return "{\"date\":\"2017-04-20T12:12:00\",\"forecasts\":"+forecasts+"}";
    }

    private void givenNoWeatherData() {

    }

    private void whenServletIsCalled() throws IOException, ServletException {
        servlet.doGet(context.request(), context.response());
        LOG.debug(context.response().getOutputAsString());
    }

    private static final Logger LOG = LoggerFactory.getLogger(WeatherForecastServletTest.class);
}
