package au.com.woolworths.core.integration.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;

import au.com.woolworths.core.integration.WeatherForcast;
import au.com.woolworths.core.integration.WeatherForcastService;
import io.marto.restclient.RestClient;

/**
 * Implementation of the {@link WeatherForcastService} using the YQL Yahoo API
 */
public class WeatherForcastServiceImpl implements WeatherForcastService {
    private static final String FORCAST_YQL = "select item from weather.forecast where woeid in (select woeid from geo.places(1) where text='%s' and country.code='%s' and (placeTypeName.content='Town' or placeTypeName.content='City')) and u='c'";
    private final RestClient<WeatherForcast> client;

    public WeatherForcastServiceImpl(RestClient<WeatherForcast> restClient) {
        this.client = restClient;
    }

    @Override
    public WeatherForcast getForecast(String area, String countryCode) {
// TODO MJP - Remove me

//        JsonNode node = client.get(JsonNode.class, Collections.singlestonMap("query", format(FORCAST_YQL, area, countryCode)));
//        LinkedList<String> path = new LinkedList<String>(Arrays.asList("query", "results", "channel", "item"));
//        while (node != null && !path.isEmpty()) {
//            node = node.get(path.pop());
//        }

//        if (node != null) {
//            ObjectMapper mapper = new ObjectMapper();
//            ObjectReader r = mapper.reader(Item.class);
//            Item item = r.readValue(node);
//            return item.getForecast();
//        }

        String yQuery = format(FORCAST_YQL, area, countryCode);
        return client.get(WeatherForcast.class, singletonMap("query", yQuery));
    }
}
