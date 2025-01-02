package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Statut;
import fr.parisnanterre.noah.Service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;

    /**
     * Récupérer les demandes pour le voyageur connecté.
     */
    @GetMapping
    public ResponseEntity<List<Demande>> getDemandesByVoyageur(Authentication authentication) {
        try {
            // Récupérer l'email de l'utilisateur connecté
            String email = authentication.getName();
            System.out.println("Authenticated email: " + email);

            // Appeler le service pour récupérer les demandes
            List<Demande> demandes = demandeService.getDemandesByVoyageur(email);
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse HTTP 400
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Créer une nouvelle demande.
     */
    @PostMapping("/{colisId}/voyageur/{voyageurId}")
    public ResponseEntity<Demande> createDemande(
            @PathVariable Long colisId,
            @PathVariable Long voyageurId,
            Authentication authentication) {
        try {
            // Récupérer l'email de l'expéditeur connecté
            String expediteurEmail = authentication.getName();

            // Créer la demande
            Demande demande = demandeService.createDemande(colisId, voyageurId, expediteurEmail);
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse HTTP 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mettre à jour le statut d'une demande.
     */
    @PutMapping("/{demandeId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long demandeId,
            @RequestBody Map<String, String> body) {
        try {
            // Récupérer et valider le statut
            String status = body.get("status");
            Statut statut = Statut.valueOf(status.toUpperCase()); // Convertir en enum

            // Mettre à jour le statut
            demandeService.updateStatus(demandeId, statut);
            return ResponseEntity.ok("Statut mis à jour avec succès.");
        } catch (IllegalArgumentException e) {
            // Si le statut est invalide
            return ResponseEntity.badRequest().body("Statut invalide : " + body.get("status"));
        } catch (Exception e) {
            // Pour toute autre erreur
            return ResponseEntity.status(500).body("Erreur lors de la mise à jour du statut.");
        }
    }
}
