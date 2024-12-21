package fr.parisnanterre.noah.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;
    private LocalDateTime dateEnvoi;
    private boolean lue;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;

    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;

    @ManyToOne
    @JoinColumn(name = "livraison_associee_id")
    private Livraison livraisonAssociee;

    @ManyToOne
    @JoinColumn(name = "notation_associee_id")
    private Notation notationAssociee;
}
