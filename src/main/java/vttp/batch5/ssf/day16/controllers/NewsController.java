package vttp.batch5.ssf.day16.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vttp.batch5.ssf.day16.models.News;
import vttp.batch5.ssf.day16.models.SearchParams;
import vttp.batch5.ssf.day16.services.NewsService;

@Controller
public class NewsController {
    @Autowired 
    NewsService newsSvc;
    
    @RequestMapping(path={"/", "index.html"})
    public String getIndex(HttpSession sess, Model model) {

        // Store to session to reduce calls (countriesList won't change)
        if(sess.getAttribute("countriesList") == null)
            sess.setAttribute("countriesList", newsSvc.getCountries());

        model.addAttribute("countries", sess.getAttribute("countriesList"));
        
        return "index";
    }


    @RequestMapping("/search")
    public String getNews(@RequestParam MultiValueMap<String, String> queryParams, Model model) {
        
        SearchParams params = new SearchParams(
                            queryParams.getFirst("query"), 
                            queryParams.getFirst("category"),
                            queryParams.getFirst("country"));

        List<News> newsResults = newsSvc.search(params);

        model.addAttribute("query", params.query());
        model.addAttribute("newsResults", newsResults);

        return "news-results";
    }
}

// Day 16 - Extra Workshop
// Get form:
// - search (string)
// - category (string) dropdown
// - country (string) dropdown using restcountries.com/v3.1/all
// > Search (btn)

// Display:
// Article
// - title 
// - img - to use placeholder img if no img found
// - description
// - content
// - link to original article