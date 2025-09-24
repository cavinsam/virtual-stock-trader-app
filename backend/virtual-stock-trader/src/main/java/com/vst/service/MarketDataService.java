package com.vst.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarketDataService {

    private final RestTemplate restTemplate;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.url}")
    private String apiUrl;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the latest quote for a given stock symbol from Alpha Vantage.
     * @param symbol The stock symbol (e.g., "AAPL", "GOOGL").
     * @return A JSON string containing the stock data.
     */
    public String getStockQuote(String symbol) {
        // Construct the URL for the GLOBAL_QUOTE function of Alpha Vantage
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        
        // Make the API call and return the response as a String
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Fetches the latest market news from Alpha Vantage.
     * @return A JSON string containing the news feed.
     */
    public String getMarketNews() {
        // Construct the URL for the NEWS_SENTIMENT function
        String url = String.format("%s?function=NEWS_SENTIMENT&topics=technology&apikey=%s", apiUrl, apiKey);

        return restTemplate.getForObject(url, String.class);
    }
}
