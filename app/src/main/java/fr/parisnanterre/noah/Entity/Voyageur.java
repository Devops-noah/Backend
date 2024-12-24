package fr.parisnanterre.noah.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false) // Add this to remove the warning
@DiscriminatorValue("voyageur")
public class Voyageur extends Utilisateur{
    // Voyageur-specific methods
    public void creerAnnonce(String paysDepart, String paysArrivee, String dateDepart, String dateArrivee) {
        // Logic to create an announcement
    }

    public void supprimerAnnonce(Long idAnnonce) {
        // Logic to delete an announcement
    }
}
