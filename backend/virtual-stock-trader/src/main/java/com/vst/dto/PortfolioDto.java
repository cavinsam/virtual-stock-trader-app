package com.vst.dto;

/**
 * A Data Transfer Object representing a user's portfolio holding.
 * This class is used to safely send portfolio data to the frontend
 * without exposing sensitive User entity details like the password.
 */
public class PortfolioDto {

    private Long id;
    private String stockSymbol;
    private int sharesOwned;
    private double averagePrice;

    // --- CONSTRUCTORS ---
    public PortfolioDto() {}

    public PortfolioDto(Long id, String stockSymbol, int sharesOwned, double averagePrice) {
        this.id = id;
        this.stockSymbol = stockSymbol;
        this.sharesOwned = sharesOwned;
        this.averagePrice = averagePrice;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public int getSharesOwned() { return sharesOwned; }
    public void setSharesOwned(int sharesOwned) { this.sharesOwned = sharesOwned; }
    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
}
