package au.com.woolworths.core.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Answers;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.RequiredArgsConstructor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/META-INF/spring/rest-template.xml", "classpath:/META-INF/spring/mock-properties.xml", "classpath:/META-INF/spring/services.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestWeatherForcastServiceImpl {

    @Autowired
    private WeatherForcastService service;

    @Autowired
    @Mock
    @ReplaceWithMock(name = "clientHttpRequestFactory-slow", beanName = "clientHttpRequestFactory-slow", defaultAnswer = Answers.RETURNS_SMART_NULLS)
    private ClientHttpRequestFactory clientHttpRequestFactory;

    @Test
    public void test_getAllMarketingBrands() throws Exception {
        givenResponse("yahoo-wheather-forecast-response.json");

        // when
        WeatherForcast value = service.getForecast("Sydney", "AU");

        // then ...
        assertNotNull(value);
        assertNotNull(value.getForecast());
        assertEquals(10, value.getForecast().size());

        verify(clientHttpRequestFactory).createRequest(StartsWithURI.startsWith("https://query.yahooapis.com/v1/public/yql?q"), eq(HttpMethod.GET));
        verify(clientHttpRequestFactory, times(1)).createRequest(any(URI.class), eq(HttpMethod.GET));
    }

    private void givenResponse(String resource) throws IOException {
        final MockClientHttpRequest request = new MockClientHttpRequest();
        request.setMethod(HttpMethod.GET);
        when(clientHttpRequestFactory.createRequest(any(URI.class), eq(HttpMethod.GET))).thenReturn(request);

        MockClientHttpResponse clientHttpResponse = new MockClientHttpResponse(getResource(resource), HttpStatus.OK);
        clientHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        request.setResponse(clientHttpResponse);
    }

    public static InputStream getResource(String resource) {
        return getResource(resource, TestWeatherForcastServiceImpl.class);
    }

    public static InputStream getResource(String resource, Class<?> cls) {
        InputStream resourceAsStream = cls.getResourceAsStream(resource);

        if (resourceAsStream == null) {
            fail("Could not locate test resource: " + resource);
        }

        return resourceAsStream;
    }

    @SuppressWarnings("serial")
    @RequiredArgsConstructor
    public static class StartsWithURI extends ArgumentMatcher<URI> implements Serializable {
        private final String prefix;

        @Override
        public boolean matches(Object actual) {
            return actual != null && ((URI) actual).toString().startsWith(prefix);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("startsWith(\"" + prefix + "\")");
        }

        public static URI startsWith(String uri) {
            return argThat(new StartsWithURI(uri));
        }
    }
}
