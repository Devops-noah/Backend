package fr.parisnanterre.noah.service;

import fr.parisnanterre.noah.Service.DemandeTransfertService;
import fr.parisnanterre.noah.Entity.Segment;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Repository.SegmentRepository;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Entity.Annonce;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)  // Intégration avec Spring
@SpringBootTest  // Chargement complet du contexte Spring Boot
class DemandeTransfertServiceTest {

    @MockBean  // Simule le repository Annonce
    private AnnonceRepository annonceRepository;

    @MockBean  // Simule le repository Segment
    private SegmentRepository segmentRepository;

    @Autowired  // Injecte le service à tester
    private DemandeTransfertService demandeTransfertService;

    private Segment segment1;
    private Segment segment2;
    private Annonce annonce1;

    @BeforeEach
    void setUp() {
        // Initialisation de l'annonce avant de l'utiliser dans les segments
        annonce1 = mock(Annonce.class);
        when(annonce1.getVoyage()).thenReturn(mock(Voyage.class));
        when(annonce1.getVoyage().getPaysDepart()).thenReturn(mock(Pays.class));
        when(annonce1.getVoyage().getPaysDestination()).thenReturn(mock(Pays.class));
        when(annonce1.getVoyage().getDateDepart()).thenReturn(new Date());
        when(annonce1.getVoyage().getDateArrivee()).thenReturn(new Date(System.currentTimeMillis() + 1000000));

        // Création d'un segment valide
        segment1 = new Segment();
        segment1.setPointDepart("Paris");
        segment1.setPointArrivee("Lyon");
        segment1.setDateDepart(new Date());
        segment1.setDateArrivee(new Date(System.currentTimeMillis() + 1000000)); // délai de 1 heure
        segment1.setAnnonce(annonce1);

        // Création d'un autre segment avec un délai invalide
        segment2 = new Segment();
        segment2.setPointDepart("Lyon");
        segment2.setPointArrivee("Marseille");
        segment2.setDateDepart(new Date(System.currentTimeMillis() + 500000)); // délai invalide
        segment2.setDateArrivee(new Date(System.currentTimeMillis() + 1000000)); // délai de 1 heure
        segment2.setAnnonce(annonce1);

        // Configuration des mocks pour les repositories
        when(annonceRepository.findActiveAnnoncesWithPays()).thenReturn(Arrays.asList(annonce1));
        when(segmentRepository.save(any(Segment.class))).thenReturn(segment1);
    }

    @Test
    @WithMockUser  // Simule un utilisateur authentifié pour tester les accès protégés
    void testRechercherSegments_ShouldReturnValidPaths_WhenValidSegmentsExist() {
        // Given
        List<Annonce> annonces = Collections.singletonList(annonce1);
        when(annonceRepository.findActiveAnnoncesWithPays()).thenReturn(annonces);
        when(segmentRepository.save(any(Segment.class))).thenReturn(segment1);

        // When
        List<List<Segment>> result = demandeTransfertService.rechercherSegments("Paris", "Lyon");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty(), "La liste des chemins ne doit pas être vide");
        assertEquals(1, result.size(), "Il doit y avoir un chemin valide");
        assertEquals("Paris", result.get(0).get(0).getPointDepart(), "Le départ du premier segment doit être Paris");
        assertEquals("Lyon", result.get(0).get(0).getPointArrivee(), "Le point d'arrivée du premier segment doit être Lyon");
    }


}
