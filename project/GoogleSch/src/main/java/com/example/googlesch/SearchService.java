package com.example.googlesch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private SearchRepository searchRepository;

    public void crawlAndSaveSearches(String keyword){
        String url = "https://scholar.google.com/scholar?q=" + keyword;

        try{
            Document doc = Jsoup.connect(url).get();
            Elements results = doc.select(".gs_r");

            List<SearchDto> searches = new ArrayList<>();

            for(Element result: results){
                SearchDto searchDto = new SearchDto();

                Element titleElement = result.selectFirst(".gs_rt a");
                searchDto.setTitle(titleElement.text());

                Element authorElement = result.selectFirst(".gs_a");
                searchDto.setAuthors(authorElement.text());

                Element yearElement = result.selectFirst(".gs_rs");
                String yearText = yearElement.text();
                int startYearIndex = yearText.indexOf("20");
                int endYearIndex = yearText.indexOf(" - ");
                String year = yearText.substring(startYearIndex, endYearIndex);
                searchDto.setPublicationYear(Integer.parseInt(year));

                Element publisherElement = result.selectFirst(".gs_pub");
                searchDto.setPublisher(publisherElement.text());

                Element linkElement = result.selectFirst(".gs_rt a");
                String link = linkElement.attr("href");
                searchDto.setLink(link);

                searches.add(searchDto);

            }

            saveSearches(searches);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSearches(List<SearchDto> searches){
        for(SearchDto searchDto : searches){
            Search search = new Search();
            search.setTitle(searchDto.getTitle());
            search.setAuthors(searchDto.getAuthors());
            search.setPublicationYear(searchDto.getPublicationYear());
            search.setPublisher(searchDto.getPublisher());
            search.setLink(searchDto.getLink());
            searchRepository.save(search);
        }
    }

    public List<Search> getAllSearches(){
        return searchRepository.findAll();
    }
}
