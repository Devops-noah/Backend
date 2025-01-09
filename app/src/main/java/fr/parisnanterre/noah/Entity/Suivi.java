package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String etatActuel;

    @ElementCollection
    private List<String> historique;

    // Méthode pour mettre à jour l'état dans le suivi
    public void mettreAJourEtat(String nouvelEtat) {
        this.etatActuel = nouvelEtat;
        historique.add("État mis à jour: " + nouvelEtat);
        // Implémentation logique pour sauvegarder l'historique
    }
}
