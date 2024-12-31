package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.InformationColisRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.Service.InformationColisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/information_colis")
@RequiredArgsConstructor
public class InformationColisController {
    private final InformationColisService informationColisService;

    @PostMapping
    public HttpEntity<InformationColisResponse> proposerColis(
            @RequestBody InformationColisRequest colisRequest) {
        try {
            // Appel au service pour traiter la demande
            InformationColisResponse response = informationColisService.proposerColis(colisRequest);
            return ResponseEntity.ok(response); // Retourner un statut 200 avec la réponse
        } catch (Exception e) {
            InformationColisResponse errorResponse = new InformationColisResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse); // Retourner un statut 400 avec l'erreur
        }
    }
}
