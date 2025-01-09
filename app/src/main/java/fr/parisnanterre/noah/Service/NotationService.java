package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.NotationDto;
import fr.parisnanterre.noah.Entity.Livraison;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Entity.StatutLivraison;
import fr.parisnanterre.noah.Repository.NotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotationService {

    private final NotationRepository notationRepository;
    private final LivraisonService livraisonService;

    // Crée une notation à partir d'une entité Notation
    public Notation createNotation(Notation notation) {
        Livraison livraison = notation.getLivraison();
        if (livraison == null || livraison.getStatut() != StatutLivraison.LIVREE) {
            throw new IllegalStateException("La notation est autorisée uniquement pour les livraisons avec un statut 'LIVREE'.");
        }

        // Calcul automatique de la note globale grâce à @PrePersist
        return notationRepository.save(notation);
    }

    // Crée une notation à partir d'un NotationDto
    public Notation createNotationFromDto(NotationDto notationDto) {
        Livraison livraison = livraisonService.getLivraisonById(notationDto.getLivraisonId());

        // Validation du statut de la livraison
        if (livraison == null || livraison.getStatut() != StatutLivraison.LIVREE) {
            throw new IllegalStateException("La notation est autorisée uniquement pour les livraisons avec un statut 'LIVREE'.");
        }

        // Mapper les champs du DTO vers une entité Notation
        Notation notation = new Notation();
       notation.setLivraison(livraison);
        notation.setNotePonctualite(notationDto.getNotePonctualite());
        notation.setNoteEtatObjet(notationDto.getNoteEtatObjet());
        notation.setNoteCommunication(notationDto.getNoteCommunication());
        notation.setCommentaire(notationDto.getCommentaire());
        // NoteGlobale sera calculée automatiquement via @PrePersist

        // Sauvegarde dans le repository
        return notationRepository.save(notation);
    }

    // Récupère les notations associées à une livraison
    public List<Notation> getNotationsByLivraison(Livraison livraison) {
        return notationRepository.findByLivraison(livraison);
    }

    // Calcul de la note globale pour une livraison
    public Double calculateGlobalNote(Livraison livraison) {
        List<Notation> notations = getNotationsByLivraison(livraison);
        if (notations.isEmpty()) {
            return null;
        }

        // Moyenne des notes globales
        return notations.stream()
                .mapToDouble(Notation::getNoteGlobale)
                .average()
                .orElse(0.0);
    }
}
