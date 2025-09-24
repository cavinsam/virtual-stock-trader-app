package com.vst.controller;

import com.vst.dto.CompetitionParticipantDto;
import com.vst.entity.Competition;
import com.vst.entity.User;
import com.vst.repository.UserRepository;
import com.vst.service.CompetitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final UserRepository userRepository;

    public CompetitionController(CompetitionService competitionService, UserRepository userRepository) {
        this.competitionService = competitionService;
        this.userRepository = userRepository;
    }

    /**
     * GET endpoint to fetch all available competitions.
     * Accessible by any authenticated user.
     */
    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

    /**
     * POST endpoint for an ADMIN to create a new competition.
     * This endpoint is protected and requires ADMIN role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Competition createCompetition(@RequestBody Competition competition) {
        return competitionService.createCompetition(competition);
    }

    /**
     * POST endpoint for any authenticated user to join a competition.
     * This now correctly returns the safe CompetitionParticipantDto.
     * @param id The ID of the competition to join, taken from the URL path.
     */
    @PostMapping("/join/{id}")
    public ResponseEntity<CompetitionParticipantDto> joinCompetition(@PathVariable Long id) {
        User currentUser = getFreshAuthenticatedUser();
        CompetitionParticipantDto participantDto = competitionService.joinCompetition(id, currentUser);
        return ResponseEntity.ok(participantDto);
    }

    /**
     * Helper method to get the fresh, fully-managed User object from the database.
     * This is the most reliable way to get the current user and avoids session issues.
     * @return The currently authenticated User entity.
     */
    private User getFreshAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}

