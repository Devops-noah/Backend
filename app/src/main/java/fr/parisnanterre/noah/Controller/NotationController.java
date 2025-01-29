package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.NotationResponse;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.DTO.NotationRequest;
import fr.parisnanterre.noah.Service.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Notation> createNotation(@RequestBody NotationRequest notationRequest /*Principal principal*/) {
        try {
            Notation notation = notationService.createNotation(notationRequest);
            return ResponseEntity.ok(notation);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // API pour récupérer les notations de l'utilisateur connecté
    @GetMapping("/user")
    public ResponseEntity<List<Notation>> getNotationsForLoggedUser(Principal principal) {
        Long utilisateurId = Long.valueOf(principal.getName());
        List<Notation> notations = notationService.getNotationsByUtilisateurId(utilisateurId);
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
