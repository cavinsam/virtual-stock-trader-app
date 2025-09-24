package com.vst.controller;

import com.vst.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /**
     * GET endpoint to fetch a quote for a specific stock.
     * @param symbol The stock symbol.
     * @return A JSON response from the external API.
     */
    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<String> getStockQuote(@PathVariable String symbol) {
        String quote = marketDataService.getStockQuote(symbol);
        return ResponseEntity.ok(quote);
    }

    /**
     * GET endpoint to fetch general market news.
     * @return A JSON response from the external API.
     */
    @GetMapping("/news")
    public ResponseEntity<String> getMarketNews() {
        String news = marketDataService.getMarketNews();
        return ResponseEntity.ok(news);
    }
}
