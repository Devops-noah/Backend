package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.DTO.DemandeResponse;  // Importer le DTO
import fr.parisnanterre.noah.DTO.DemandeRequest;
import fr.parisnanterre.noah.Entity.Statut;
import fr.parisnanterre.noah.Service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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

//    // Récupérer toutes les demandes du voyageur
//    @GetMapping("/demande/{voyageurId}")
//    public HttpEntity<List<Demande>> getDemandesByVoyageur(@PathVariable String voyageurId) {
//        try {
//            List<Demande> demandes = demandeService.getDemandesByVoyageur(voyageurId);
//            return ResponseEntity.ok(demandes);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }


    /**
     * Récupérer les demandes pour l'expéditeur connecté.
     */
    @GetMapping
    public ResponseEntity<?> getDemandesByExpediteur(Authentication authentication) {
        try {
            // Récupérer l'email de l'utilisateur connecté (l'expéditeur)
            String email = authentication.getName();

            // Appeler le service pour récupérer les demandes de cet expéditeur
            List<DemandeResponse> demandes = demandeService.getDemandesByExpediteur(email);

            // Retourner la liste des demandes si tout va bien
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException e) {
            // En cas d'erreur d'accès non autorisé ou autre exception métier, retourner un message clair
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous devez être connecté en tant qu'expéditeur pour accéder aux demandes.");
        } catch (Exception e) {
            // Pour toute autre erreur, retourner une réponse générique
            return ResponseEntity.badRequest().body("Une erreur est survenue lors de la récupération des demandes.");
        }
    }



    /**
     * Créer une nouvelle demande (l'expéditeur crée la demande pour son colis).
     */
    @PostMapping("/{colisId}")
    public ResponseEntity<DemandeResponse> createDemande(
            @PathVariable Long colisId,
            @RequestBody DemandeRequest demandeRequest,
            Authentication authentication) {
        System.out.println("Authentication: " + authentication);

        try {

            // Vérifier que l'utilisateur est authentifié
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body(null); // Retourner 403 si non authentifié
            }

            // Récupérer l'email de l'expéditeur authentifié
            String expediteurEmail = authentication.getName();

            // Créer la demande avec l'email de l'expéditeur
            DemandeResponse demandeResponse = demandeService.createDemande(demandeRequest, colisId, expediteurEmail);

            return ResponseEntity.ok(demandeResponse); // Retourner la demande en réponse
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Si une erreur se produit, retour de l'erreur 400
        }
    }

    /**
     * Tester la création de demande en passant directement les paramètres (sans authentification).
     */
//    @PostMapping("/testDemande")
//    public ResponseEntity<DemandeResponse> testCreateDemande(@RequestParam Long colisId, @RequestParam String expediteurEmail) {
//        System.out.println("Test de la création de demande");
//        try {
//            DemandeResponse response = demandeService.createDemande(demandeRcolisId, expediteurEmail);
//            System.out.println("Demande créée avec succès: " + response);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            System.out.println("Erreur lors de la création de la demande: " + e.getMessage());
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

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
