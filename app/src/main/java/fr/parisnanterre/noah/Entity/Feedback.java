package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message; // Feedback message content

    @Enumerated(EnumType.STRING)
    private FeedbackType type; // Confirmation or Erreur

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = true)
    private Utilisateur utilisateur; // Optional relationship to Utilisateur
}
