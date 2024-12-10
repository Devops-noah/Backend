package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("expediteur")
public class Expediteur extends Utilisateur{
    // Expediteur-specific methods
    public void rechercherAnnonces(String filtre) {
        // Logic for searching announcements
    }

    public void envoyerDemande(Long idAnnonce) {
        // Logic to send a request
    }
}


