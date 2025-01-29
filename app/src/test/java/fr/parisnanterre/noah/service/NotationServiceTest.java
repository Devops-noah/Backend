package fr.parisnanterre.noah.service;

import fr.parisnanterre.noah.DTO.NotationRequest;
import fr.parisnanterre.noah.DTO.NotationResponse;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Entity.StatutNotation;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.NotationRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.NotationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotationServiceTest {

    @Mock
    private NotationRepository notationRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private NotationService notationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotation() throws Exception {
        // Arrange : Mock de l'utilisateur
        Utilisateur mockUtilisateur = mock(Utilisateur.class);
        when(mockUtilisateur.getId()).thenReturn(1L);
        when(mockUtilisateur.getNom()).thenReturn("Doe");
        when(mockUtilisateur.getPrenom()).thenReturn("John");

        NotationRequest notationRequest = new NotationRequest();
        notationRequest.setUtilisateurId(1L);
        notationRequest.setNote(5);
        notationRequest.setCommentaire("Très bon service !");
        notationRequest.setDatePublication(LocalDate.now().toString());

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(mockUtilisateur));

        Notation mockNotation = new Notation();
        mockNotation.setNote(5);
        mockNotation.setCommentaire("Très bon service !");
        mockNotation.setDatePublication(LocalDate.now().toString());
        mockNotation.setUtilisateur(mockUtilisateur);

        when(notationRepository.save(any(Notation.class))).thenReturn(mockNotation);

        // Act
        Notation result = notationService.createNotation(notationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getNote());
        assertEquals("Très bon service !", result.getCommentaire());
        verify(notationRepository, times(1)).save(any(Notation.class));
    }

    @Test
    void testGetAllNotationsWithApprovedComments() {
        // Arrange : Mock de l'utilisateur
        Utilisateur mockUtilisateur = mock(Utilisateur.class);
        when(mockUtilisateur.getId()).thenReturn(1L);
        when(mockUtilisateur.getNom()).thenReturn("Doe");
        when(mockUtilisateur.getPrenom()).thenReturn("John");

        Notation approvedNotation = new Notation();
        approvedNotation.setNote(4);
        approvedNotation.setCommentaire("Bon produit");
        approvedNotation.setDatePublication(LocalDate.now().toString());
        approvedNotation.setUtilisateur(mockUtilisateur);
        approvedNotation.setStatus(StatutNotation.APPROVED);

        when(notationRepository.findAll()).thenReturn(List.of(approvedNotation));

        // Act
        List<NotationResponse> result = notationService.getAllNotationsWithApprovedComments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bon produit", result.get(0).getCommentaire());
        verify(notationRepository, times(1)).findAll();
    }
}
