package com.vst.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Lazy fetch for performance
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_symbol", nullable = false)
    private String stockSymbol;

    @Column(name = "shares_owned", nullable = false)
    private int sharesOwned;

    @Column(name = "average_price", nullable = false)
    private double averagePrice;

    // --- CONSTRUCTORS ---
    public Portfolio() {}

    public Portfolio(User user, String stockSymbol, int sharesOwned, double averagePrice) {
        this.user = user;
        this.stockSymbol = stockSymbol;
        this.sharesOwned = sharesOwned;
        this.averagePrice = averagePrice;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public int getSharesOwned() { return sharesOwned; }
    public void setSharesOwned(int sharesOwned) { this.sharesOwned = sharesOwned; }

    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
}
