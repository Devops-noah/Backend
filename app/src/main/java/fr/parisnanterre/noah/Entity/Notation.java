package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notation {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "livraison_id", nullable = false)
    private Livraison livraison;

    private int notePonctualite;
    private int noteEtatObjet;
    private int noteCommunication;

    private double noteGlobale;

    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur; // L'utilisateur qui a donné cette notation

    public double calculateNoteGlobale() {
        return (notePonctualite + noteEtatObjet + noteCommunication) / 3.0;
    }

    // Vérifie si la livraison est "LIVREE" avant de sauvegarder la notation
    @PrePersist
    @PreUpdate
    public void preSave() {
        if (livraison == null || livraison.getStatut() != StatutLivraison.LIVREE) {
            throw new IllegalStateException("La notation est autorisée uniquement pour les livraisons avec un statut 'LIVREE'.");
        }
        this.noteGlobale = calculateNoteGlobale();
    }*/
}