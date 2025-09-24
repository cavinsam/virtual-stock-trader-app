package com.vst.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Better performance
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_symbol", nullable = false) // ✅ safer to mark NOT NULL since always set
    private String stockSymbol;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // BUY / SELL

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double pricePerShare;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // --- CONSTRUCTORS ---
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(User user, String stockSymbol, String transactionType, int quantity, double pricePerShare) {
        this.user = user;
        this.stockSymbol = stockSymbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now();
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPricePerShare() { return pricePerShare; }
    public void setPricePerShare(double pricePerShare) { this.pricePerShare = pricePerShare; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
