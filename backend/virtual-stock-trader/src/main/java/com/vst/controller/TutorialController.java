package com.vst.controller;

import com.vst.entity.Tutorial;
import com.vst.repository.TutorialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutorials")
public class TutorialController {

    private final TutorialRepository tutorialRepository;

    public TutorialController(TutorialRepository tutorialRepository) {
        this.tutorialRepository = tutorialRepository;
    }

    /**
     * GET endpoint to fetch all tutorials. This is public.
     */
    @GetMapping
    public List<Tutorial> getAllTutorials() {
        return tutorialRepository.findAll();
    }

    /**
     * POST endpoint to create a new tutorial.
     * Only users with the 'ADMIN' role can access this.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Tutorial createTutorial(@RequestBody Tutorial tutorial) {
        return tutorialRepository.save(tutorial);
    }

    /**
     * DELETE endpoint to remove a tutorial.
     * Only users with the 'ADMIN' role can access this.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTutorial(@PathVariable Long id) {
        return tutorialRepository.findById(id).map(tutorial -> {
            tutorialRepository.delete(tutorial);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}