package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean isRead;

    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "voyageur_id", nullable = false)
    private Utilisateur voyageur;

    @ManyToOne
    @JoinColumn(name = "information_colis_id", nullable = false)
    private InformationColis informationColis; // Lien avec InformationColis
}
