package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int livraisonId;
    private int notePonctualite;
    private int noteEtatObjet;
    private int noteCommunication;
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

}
