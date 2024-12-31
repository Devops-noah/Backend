package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.InformationColisRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.Service.InformationColisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/information_colis")
@RequiredArgsConstructor
public class InformationColisController {
    private final InformationColisService informationColisService;

    @PostMapping("/{annonceId}")
    public HttpEntity<InformationColisResponse> proposerColis(
            @PathVariable Long annonceId,
            @RequestBody InformationColisRequest colisRequest,
            Authentication authentication) {
        try {
            // Retrieve the authenticated user's email
            String email = authentication.getName();
            System.out.println("Authenticated email: " + email); // Log the email for debugging

            InformationColisResponse response = informationColisService.proposerColis(colisRequest, email, annonceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            InformationColisResponse errorResponse = new InformationColisResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
