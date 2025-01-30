package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.NotationResponse;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.DTO.NotationRequest;
import fr.parisnanterre.noah.Service.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notations")
public class NotationController {

    private final NotationService notationService;

    @Autowired
    public NotationController(NotationService notationService) {
        this.notationService = notationService;
    }

    // API pour créer une nouvelle notation
    @PostMapping("/create")
    public ResponseEntity<?> createNotation(@RequestBody NotationRequest notationRequest /*Principal principal*/) {
        try {
            Notation notation = notationService.createNotation(notationRequest);
            return ResponseEntity.ok(notation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // API pour récupérer les notations de l'utilisateur connecté
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notation>> getNotationsByUserId(@PathVariable Long userId) {
        List<Notation> notations = notationService.getNotationsByUtilisateurId(userId);
        return ResponseEntity.ok(notations);
    }
    // API pour récupérer toutes les notations approuvées
    @GetMapping("/approved")
    public ResponseEntity<List<NotationResponse>> getAllNotationsWithApprovedComments() {
        List<NotationResponse> notations = notationService.getAllNotationsWithApprovedComments();
        if (notations.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204: No Content
        }
        return ResponseEntity.ok(notations);
    }

    // API pour récupérer les 3 dernières notations approuvées
    @GetMapping("/last-three-approved")
    public ResponseEntity<List<NotationResponse>> getLastThreeNotationsWithApprovedComments() {
        List<NotationResponse> responses = notationService.getLastThreeNotationsWithApprovedComments();
        return ResponseEntity.ok(responses);
    }
}
