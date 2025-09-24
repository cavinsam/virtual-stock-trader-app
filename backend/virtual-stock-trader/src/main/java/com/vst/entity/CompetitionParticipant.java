package com.vst.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "competition_participants")
public class CompetitionParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double portfolioValue;

    // --- CONSTRUCTORS, GETTERS, SETTERS ---
    public CompetitionParticipant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public double getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(double portfolioValue) { this.portfolioValue = portfolioValue; }
}
