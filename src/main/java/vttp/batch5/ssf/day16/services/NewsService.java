package vttp.batch5.ssf.day16.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.ssf.day16.models.Country;
import vttp.batch5.ssf.day16.models.News;
import vttp.batch5.ssf.day16.models.SearchParams;

@Service
public class NewsService {
    @Value("${api.key}")
    private String APIkey;

    public static final String COUNTRIES_URL = "https://restcountries.com/v3.1/all";
    public static final String GET_NEWS_URL = "https://newsapi.org/v2/top-headlines";
    
    public List<Country> getCountries() {
        String url = UriComponentsBuilder.fromUriString(COUNTRIES_URL)
                    .queryParam("fields", "cca2,name")
                    .toUriString();

        RequestEntity<Void> req = RequestEntity.get(url)
                                .accept(MediaType.APPLICATION_JSON)     
                                .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp;
                
        List<Country> countriesList = new ArrayList<>();

        try {
            // Make call
            resp = template.exchange(req, String.class);
            // Extract payload
            String payload = resp.getBody();
            JsonReader reader = Json.createReader(new StringReader(payload));
            JsonArray result = reader.readArray();

            // Iterate over result arr
            for(int i = 0; i < result.size(); i++) {
                JsonObject nameObj = result.getJsonObject(i).getJsonObject("name");
                String name = nameObj.getString("common");

                String code = result.getJsonObject(i).getString("cca2");

                countriesList.add(new Country(code, name));
            }
        } catch(Exception ex) {
            // Handle error
            ex.printStackTrace();
        }

        // Sort countries by name (asc)
        countriesList.sort(Comparator.comparing(Country::name));

        return countriesList;
    }


    public List<News> search(SearchParams params) {
        String url = UriComponentsBuilder.fromUriString(GET_NEWS_URL)
                    .queryParam("q", params.query())
                    .queryParam("category", params.category())
                    .queryParam("country", params.country())
                    .toUriString();

        //System.out.printf("URL with query params: \n%s\n", url);

        RequestEntity<Void> req = RequestEntity
                    .get(url)
                    .header("X-Api-Key", APIkey)
                    .accept(MediaType.APPLICATION_JSON)     
                    .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp;
        List<News> newsList = new ArrayList<>();
        
        try {
            // Make call
            resp = template.exchange(req, String.class);
            // Extract payload
            String payload = resp.getBody();
            JsonReader reader = Json.createReader(new StringReader(payload));
            JsonObject result = reader.readObject();

            JsonArray articles = result.getJsonArray("articles");

            // Iterate over data arr
            for(int i = 0; i < articles.size(); i++) {
                JsonObject article = articles.getJsonObject(i);

                String title = article.getString("title");
                String desc = article.getString("description");
                String articleURL = article.getString("url");
                String imgURL = article.getString("urlToImage");
                String content = article.getString("content");

                newsList.add(new News(title, imgURL, desc, content, articleURL));
            }
        } catch(Exception ex) {
            // Handle error
            ex.printStackTrace();
        }
        
        return newsList;
    }
}
