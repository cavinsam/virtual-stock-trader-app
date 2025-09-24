package com.vst.dto;

/**
 * A DTO for safely sending competition participant data to the client.
 * It prevents the exposure of the full User entity.
 */
public class CompetitionParticipantDto {

    private Long id;
    private Long competitionId;
    private String competitionName;
    private String username; // Only show the user's name, not the whole object
    private double portfolioValue;

    // --- CONSTRUCTORS ---
    public CompetitionParticipantDto() {}

    public CompetitionParticipantDto(Long id, Long competitionId, String competitionName, String username, double portfolioValue) {
        this.id = id;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.username = username;
        this.portfolioValue = portfolioValue;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public double getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(double portfolioValue) { this.portfolioValue = portfolioValue; }
}
