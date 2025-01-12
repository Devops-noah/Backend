package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;  // Message de notification

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "voyageur_id", nullable = false)
    private Utilisateur voyageur;  // Voyageur qui reçoit la notification

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;  // Lien vers la Demande pour laquelle la notification a été créée

    private boolean isRead = false;  // Statut de la notification : lue ou non lue

    private Date createdAt;  // Date de création de la notification
}
