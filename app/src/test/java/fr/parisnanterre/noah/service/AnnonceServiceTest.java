/*package fr.parisnanterre.noah.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

class AnnonceServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private VoyageRepository voyageRepository;

    @Mock
    private AnnonceRepository annonceRepository;

    @InjectMocks
    private AnnonceServiceImpl annonceServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAnnonce_Success() throws ParseException {
        // Mock input data
        String email = "dona@dona.com";
        Integer voyageId = 3;
        AnnonceRequest annonceRequest = new AnnonceRequest();

        // Given a date string to compare
        String expectedDate = "2025-01-29";

        // Create the SimpleDateFormat object with the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Parse the expected date string into a Date object
        Date datePublication = sdf.parse(expectedDate);  // "2025-01-29"

        // Format the actual Date object to match the string format "yyyy-MM-dd"
        String actualFormattedDate = sdf.format(datePublication);  // Should return "2025-01-29"

        // Assert both formatted date strings match
        assertEquals(expectedDate, actualFormattedDate);  // Both should be "2025-01-29"

        // Use the Date object for the method that expects Date
        annonceRequest.setDatePublication(datePublication);
        annonceRequest.setPoidsDisponible(10.5);

        Voyageur voyageur = new Voyageur();
        voyageur.setEmail(email);

        Voyage voyage = new Voyage();
        voyage.setId(voyageId);
        voyage.setVoyageur(voyageur);

        // ✅ Create and set PaysDepart
        Pays paysDepart = new Pays();
        paysDepart.setNom("France");
        voyage.setPaysDepart(paysDepart); // ✅ Now it's not null

        // ✅ Create and set PaysDestination
        Pays paysDestination = new Pays();
        paysDestination.setNom("Germany");
        voyage.setPaysDestination(paysDestination); // ✅ Now it's not null

        Annonce annonce = new Annonce();

        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(voyageur));
        when(voyageRepository.findById(voyageId)).thenReturn(Optional.of(voyage));
        when(annonceRepository.save(any(Annonce.class))).thenAnswer(invocation -> {
            Annonce savedAnnonce = invocation.getArgument(0);
            savedAnnonce.setId(1L);
            return savedAnnonce;
        });

        // Call the method
        AnnonceResponse response = annonceServiceImpl.createAnnonce(annonce, annonceRequest, email, voyageId);

        // Format the response date the same way
        String responseFormattedDate = sdf.format(response.getDatePublication());

        // Verify response
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(expectedDate, responseFormattedDate);  // Compare formatted dates
        assertEquals(10.5, response.getPoidsDisponible());
        assertEquals(voyageId, response.getVoyageId());
    }


    @Test
    void createAnnonce_UserNotFound() {
        when(utilisateurRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                annonceServiceImpl.createAnnonce(new Annonce(), new AnnonceRequest(), "wrong@example.com", 1));

        assertEquals("Voyageur not found", exception.getMessage());
    }

    @Test
    void createAnnonce_UserIsNotVoyageur() {
        Utilisateur user = new Utilisateur() {};  // Anonymous subclass (Alternative, but not recommended)
        user.setEmail("notVoyageur@example.com");

        when(utilisateurRepository.findByEmail("notVoyageur@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () ->
                annonceServiceImpl.createAnnonce(new Annonce(), new AnnonceRequest(), "notVoyageur@example.com", 1));

        assertEquals("Only Voyageurs can create annonces", exception.getMessage());
    }


    @Test
    void createAnnonce_VoyageNotFound() {
        Voyageur voyageur = new Voyageur();
        voyageur.setEmail("voyageur@example.com");

        when(utilisateurRepository.findByEmail("voyageur@example.com")).thenReturn(Optional.of(voyageur));
        when(voyageRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                annonceServiceImpl.createAnnonce(new Annonce(), new AnnonceRequest(), "voyageur@example.com", 1));

        assertEquals("Voyage with ID 1 not found", exception.getMessage());
    }

    @Test
    void createAnnonce_VoyageNotBelongToUser() {
        Voyageur voyageur = new Voyageur();
        voyageur.setEmail("voyageur@example.com");

        Voyage voyage = new Voyage();
        voyage.setId(1);
        voyage.setVoyageur(new Voyageur()); // Different owner

        when(utilisateurRepository.findByEmail("voyageur@example.com")).thenReturn(Optional.of(voyageur));
        when(voyageRepository.findById(1)).thenReturn(Optional.of(voyage));

        Exception exception = assertThrows(RuntimeException.class, () ->
                annonceServiceImpl.createAnnonce(new Annonce(), new AnnonceRequest(), "voyageur@example.com", 1));

        assertEquals("The Voyage does not belong to the logged-in Voyageur", exception.getMessage());
    }
}

*/