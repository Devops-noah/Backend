// Voyage service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.VoyageRequest;
import fr.parisnanterre.noah.DTO.VoyageResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.PaysRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoyageServiceImpl {

    private final VoyageRepository voyageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaysRepository paysRepository;
    @Autowired
    public VoyageServiceImpl(VoyageRepository voyageRepository, PaysRepository paysRepository, UtilisateurRepository utilisateurRepository) {
        this.voyageRepository = voyageRepository;
        this.paysRepository = paysRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Fetch all voyages and map them to VoyageResponse
    public List<VoyageResponse> getAllVoyages() {
        List<Voyage> voyages = voyageRepository.findAll();
        return voyages.stream().map(this::mapToVoyageResponse).collect(Collectors.toList());
    }

    // Map a Voyage entity to a VoyageResponse DTO
    private VoyageResponse mapToVoyageResponse(Voyage voyage) {
        VoyageResponse response = new VoyageResponse();
        response.setId(voyage.getId());
        response.setDateDepart(voyage.getDateDepart());
        response.setDateArrivee(voyage.getDateArrivee());

        // Map Pays (Departure and Destination)
        if (voyage.getPaysDepart() != null) {
            response.setPaysDepartId(Math.toIntExact(voyage.getPaysDepart().getId()));
            response.setPaysDepartNom(voyage.getPaysDepart().getNom());
        }

        if (voyage.getPaysDestination() != null) {
            response.setPaysDestinationId(Math.toIntExact(voyage.getPaysDestination().getId()));
            response.setPaysDestinationNom(voyage.getPaysDestination().getNom());
        }

        // Map Voyageur (User)
        if (voyage.getVoyageur() != null) {
            response.setVoyageurId(Math.toIntExact(voyage.getVoyageur().getId()));
            response.setVoyageurNom(voyage.getVoyageur().getNom());
            response.setVoyageurEmail(voyage.getVoyageur().getEmail());
        }

        // Map Annonces
        if (voyage.getAnnonces() != null) {
            response.setAnnonces(voyage.getAnnonces().stream().map(annonce -> {
                AnnonceResponse annonceResponse = new AnnonceResponse();
                annonceResponse.setId(annonce.getId());
                annonceResponse.setDatePublication(annonce.getDatePublication());
                annonceResponse.setPoidsDisponible(annonce.getPoidsDisponible());
                return annonceResponse;
            }).collect(Collectors.toList()));
            System.out.println("get annonce response: " + response.getAnnonces());
        }

        return response;
    }

    public Optional<Voyage> getVoyageById(Integer id) {
        return voyageRepository.findById(id);
    }


    @Transactional
    public VoyageResponse createVoyage(Voyage voyage, String paysDepart, String paysDestination, String email) {
        // Find the Logged-in user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        System.out.println("Before update - User types: " + utilisateur.getUserTypes());

        // Ensure the user is a Voyageur, if not, make them one
        if (!utilisateur.isVoyageur()) {
            utilisateur.becomeVoyageur();
            utilisateurRepository.save(utilisateur); // Persist the change
            System.out.println("User is now a Voyageur");
        }

        System.out.println("After update - User types: " + utilisateur.getUserTypes());

        // Check if the departure country is provided and valid
        if (paysDepart == null || paysDepart.isEmpty()) {
            throw new RuntimeException("Pays depart is missing");
        }
        Pays paysDepartNom = paysRepository.findByNom(paysDepart)
                .orElseThrow(() -> new RuntimeException("Pays depart with Name " + paysDepart + " not found"));

        // Check if the destination country is provided and valid
        if (paysDestination == null || paysDestination.isEmpty()) {
            throw new RuntimeException("Pays destination is missing");
        }
        Pays paysDestinationNom = paysRepository.findByNom(paysDestination)
                .orElseThrow(() -> new RuntimeException("Pays destination with Name " + paysDestination + " not found"));

        // Set the countries as destination for the Voyage
        voyage.setPaysDepart(paysDepartNom);
        voyage.setPaysDestination(paysDestinationNom);

        // Set the logged-in Voyageur as the owner of the Voyage
        voyage.setVoyageur(utilisateur);

        System.out.println("Voyage value: " + voyage);

        // Save the Voyage entity
        Voyage savedVoyage = voyageRepository.save(voyage);

        // Check if Annonces are linked correctly
        System.out.println("Voyage annonces after save: " + savedVoyage.getAnnonces());

        // Return the saved Voyage with country names
        return new VoyageResponse(savedVoyage);
    }



    // Update voyage
    @Transactional
    public Voyage updateVoyage(Integer voyageId, VoyageRequest voyageRequest, String email) {
        // Find the logged-in user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        // Ensure the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can update voyages");
        }

        // Find the voyage to update
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new RuntimeException("Voyage with ID " + voyageId + " not found"));

        // Ensure the logged-in Voyageur is the owner of the voyage
        if (!voyage.getVoyageur().equals(utilisateur)) {
            throw new RuntimeException("You can only update voyages you own");
        }

        // Update voyage details
        if (voyageRequest.getPaysDepart() != null && !voyageRequest.getPaysDepart().isEmpty()) {
            Pays paysDepart = paysRepository.findByNom(voyageRequest.getPaysDepart())
                    .orElseThrow(() -> new RuntimeException("Pays depart not found"));
            voyage.setPaysDepart(paysDepart);
        }

        if (voyageRequest.getPaysDestination() != null && !voyageRequest.getPaysDestination().isEmpty()) {
            Pays paysDestination = paysRepository.findByNom(voyageRequest.getPaysDestination())
                    .orElseThrow(() -> new RuntimeException("Pays destination not found"));
            voyage.setPaysDestination(paysDestination);
        }

        if (voyageRequest.getVoyage().getDateDepart() != null) {
            voyage.setDateDepart(voyageRequest.getVoyage().getDateDepart());
        }

        if (voyageRequest.getVoyage().getDateArrivee() != null) {
            voyage.setDateArrivee(voyageRequest.getVoyage().getDateArrivee());
        }

        // Save and return the updated voyage
        return voyageRepository.save(voyage);
    }

    // Delete voyage
    @Transactional
    public void deleteVoyage(Integer voyageId, String email) {
        // Find the logged-in user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        // Ensure the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can delete voyages");
        }

        // Find the voyage to delete
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new RuntimeException("Voyage with ID " + voyageId + " not found"));

        // Ensure the logged-in Voyageur is the owner of the voyage
        if (!voyage.getVoyageur().equals(utilisateur)) {
            throw new RuntimeException("You can only delete voyages you own");
        }

        // Delete the voyage
        voyageRepository.delete(voyage);
    }



}

