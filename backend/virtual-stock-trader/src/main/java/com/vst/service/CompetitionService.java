package com.vst.service;

import com.vst.dto.CompetitionParticipantDto;
import com.vst.entity.Competition;
import com.vst.entity.CompetitionParticipant;
import com.vst.entity.User;
import com.vst.repository.CompetitionParticipantRepository;
import com.vst.repository.CompetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;

    public CompetitionService(CompetitionRepository competitionRepository, CompetitionParticipantRepository participantRepository) {
        this.competitionRepository = competitionRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Retrieves a list of all competitions.
     * @return a list of Competition objects.
     */
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    /**
     * Creates a new competition.
     * @param competition The competition object to be saved.
     * @return The saved Competition entity.
     */
    public Competition createCompetition(Competition competition) {
        // In a real application, you would add validation logic here
        return competitionRepository.save(competition);
    }

    /**
     * Allows a user to join an existing competition and returns a safe DTO.
     * @param competitionId The ID of the competition to join.
     * @param user The user who is joining.
     * @return The newly created CompetitionParticipant record, converted to a safe DTO.
     */
    public CompetitionParticipantDto joinCompetition(Long competitionId, User user) {
        // Find the competition the user wants to join by its ID
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + competitionId));

        // Optional: Add logic here to prevent a user from joining the same competition twice.

        // Create a new participant entry to be saved in the database
        CompetitionParticipant participant = new CompetitionParticipant();
        participant.setCompetition(competition);
        participant.setUser(user);
        // Set the initial portfolio value to the competition's starting balance
        participant.setPortfolioValue(competition.getStartingBalance());

        // Save the new participant record to the database
        CompetitionParticipant savedParticipant = participantRepository.save(participant);
        
        // Convert the saved entity to a safe DTO before returning it to the controller
        return convertToDto(savedParticipant);
    }

    /**
     * A private helper method to convert the CompetitionParticipant entity into a
     * CompetitionParticipantDto. This is the core of the security measure.
     * @param participant The entity object saved in the database.
     * @return A new DTO containing only the data that is safe to send to the client.
     */
    private CompetitionParticipantDto convertToDto(CompetitionParticipant participant) {
        return new CompetitionParticipantDto(
            participant.getId(),
            participant.getCompetition().getId(),
            participant.getCompetition().getName(),
            participant.getUser().getRealUsername(), // Uses the safe, non-email username
            participant.getPortfolioValue()
        );
    }
}

