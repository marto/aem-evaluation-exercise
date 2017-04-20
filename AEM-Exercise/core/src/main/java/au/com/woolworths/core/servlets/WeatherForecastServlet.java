package au.com.woolworths.core.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import au.com.woolworths.core.integration.model.Forecast;

/**
 * Retrieves the weather forecast for the next 10 days from the repository if available.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/apis/weather.json")
public class WeatherForecastServlet extends SlingSafeMethodsServlet {

    // Thread safe Object writer
    private ObjectWriter objectSerializer;

    @Override
    public void init() throws ServletException {
        super.init();
        this.objectSerializer = new ObjectMapper().writer();
    }

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("utf-8");

        // Cache control for 5 minutes
        res.setHeader("Cache-Control", "max-age=600, public");

        objectSerializer.writeValue(res.getWriter(), getForecasts(req.getResourceResolver(), LocalDate.now().plusDays(1)));
    }

    private List<Forecast> getForecasts(ResourceResolver resolver, LocalDate tomorrow) {
        List<Forecast> ret = new ArrayList<Forecast>();
        LocalDate date = tomorrow;
        for (int i = 0; i < 10; i++) {
            Forecast forecast = getForecast(resolver, date);
            if (forecast != null) {
                ret.add(forecast);
            }
            date = date.plusDays(1);
        }
        return ret;
    }

    private Forecast getForecast(ResourceResolver resolver, LocalDate pointInTime) {
        return resolver.resolve("/content/weather/"+ pointInTime.format(DATE_FORMAT)).adaptTo(Forecast.class);
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
}
