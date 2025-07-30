package net.engineeringdigest.journalApp.service;


import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {


    @Value("${weather_api_key}")
    private String apikey;
    //private static final  String apikey = "baba90ef877fb12e20281d0fbc0fc11a";

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;


    public WeatherResponse getWeather(String city)
    {
        String finalAPI = appCache.appC.get(AppCache.keys.WEATHER_API.name()).replace(Placeholders.CITY,city).replace(Placeholders.API_KEY,apikey);
        final ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body=response.getBody();
        return body;
    }
}