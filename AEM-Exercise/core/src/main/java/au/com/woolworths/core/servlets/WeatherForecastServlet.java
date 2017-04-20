package au.com.woolworths.core.servlets;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import au.com.woolworths.core.integration.model.Forecast;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Retrieves the weather forecast for the next 10 days from the repository if available.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/apis/weather.json")
public class WeatherForecastServlet extends SlingSafeMethodsServlet {

    // Thread safe Object writer
    private ObjectWriter objectSerializer;
    @Setter(value=AccessLevel.PACKAGE)
    private Clock clock = Clock.systemDefaultZone();

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

        objectSerializer.writeValue(res.getWriter(), getForecasts(req.getResourceResolver(), LocalDate.now(clock).plusDays(1)));
    }

    private Data getForecasts(ResourceResolver resolver, LocalDate tomorrow) {
        final Data data = new Data(LocalDateTime.now(clock));
        LocalDate date = tomorrow;
        for (int i = 0; i < 10; i++) {
            Forecast forecast = getForecast(resolver, date);
            if (forecast != null) {
                data.addItem(forecast);
            }
            date = date.plusDays(1);
        }
        return data;
    }

    private Forecast getForecast(ResourceResolver resolver, LocalDate pointInTime) {
        final Resource resource = resolver.getResource("/content/weather/"+ pointInTime.format(DATE_FORMAT));
        return resource == null ? null : resource.adaptTo(Forecast.class);
    }

    @Getter
    public static class Data {
        private final String date;
        private final List<Forecast> forecasts = new ArrayList<Forecast>();

        private Data(LocalDateTime now) {
            this.date = DateTimeFormatter.ISO_DATE_TIME.format(now);
        }

        private void addItem(Forecast item) {
            this.forecasts.add(item);
        }
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
}
