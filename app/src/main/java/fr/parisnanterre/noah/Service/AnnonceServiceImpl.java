// Annonce service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.PaysRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ComponentScan
public class AnnonceServiceImpl {


    private final AnnonceRepository annonceRepository;
    private final PaysRepository paysRepository;
    private final VoyageRepository voyageRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public AnnonceServiceImpl(AnnonceRepository annonceRepository, PaysRepository paysRepository, VoyageRepository voyageRepository, UtilisateurRepository utilisateurRepository) {
        this.annonceRepository = annonceRepository;
        this.paysRepository = paysRepository;
        this.voyageRepository = voyageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<AnnonceResponse> getAllAnnonces() {
        List<Annonce> annonces = annonceRepository.findAllWithVoyage();

        // Map Annonce to fr.parisnanterre.noah.DTO.AnnonceRequest.AnnonceResponse
        return annonces.stream().map(annonce -> {
            AnnonceResponse response = new AnnonceResponse();
            response.setId(annonce.getId());
            response.setDatePublication(annonce.getDatePublication());
            response.setPoidsDisponible(annonce.getPoidsDisponible());

            // Retrieve Voyage information
            if (annonce.getVoyage() != null) {
                Voyage voyage = annonce.getVoyage();

                // Use dateDepart and dateArrivee from the Voyage
                response.setDateDepart(voyage.getDateDepart());
                response.setDateArrivee(voyage.getDateArrivee());

                // Retrieve paysDepart and paysDestination from the Voyage
                response.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart().getNom() : null);
                response.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination().getNom() : null);

                // Set Voyage ID
                response.setVoyageId(voyage.getId());
            } else {
                // Handle case where Voyage is null
                response.setDateDepart(null);
                response.setDateArrivee(null);
                response.setPaysDepart(null);
                response.setPaysDestination(null);
                response.setVoyageId(null);
            }
            // Retrieve Voyageur information using UtilisateurRepository
            if (annonce.getVoyageur() != null) {
                // Assuming the Voyageur entity has getId() and getName() methods
                response.setVoyageurId(Math.toIntExact(annonce.getVoyageur().getId()));
                response.setVoyageurNom(annonce.getVoyageur().getNom());
                response.setVoyageurEmail(annonce.getVoyageur().getEmail());
            }



            return response;
        }).collect(Collectors.toList());
    }

    public Optional<AnnonceResponse> getAnnonceById(Integer id) {
        return annonceRepository.findById(id)
                .map(annonce -> {
                    AnnonceResponse response = new AnnonceResponse();
                    response.setId(annonce.getId());
                    response.setDatePublication(annonce.getDatePublication());
                    response.setPoidsDisponible(annonce.getPoidsDisponible());

                    // Retrieve Voyage information
                    if (annonce.getVoyage() != null) {
                        Voyage voyage = annonce.getVoyage();

                        // Use dateDepart and dateArrivee from the Voyage
                        response.setDateDepart(voyage.getDateDepart());
                        response.setDateArrivee(voyage.getDateArrivee());

                        // Retrieve paysDepart and paysDestination from the Voyage
                        response.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart().getNom() : null);
                        response.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination().getNom() : null);

                        // Set Voyage ID
                        response.setVoyageId(voyage.getId());
                    } else {
                        // Handle case where Voyage is null
                        response.setDateDepart(null);
                        response.setDateArrivee(null);
                        response.setPaysDepart(null);
                        response.setPaysDestination(null);
                        response.setVoyageId(null);
                    }

                    // Retrieve Voyageur information using UtilisateurRepository
                    if (annonce.getVoyageur() != null) {
                        // Assuming the Voyageur entity has getId() and getName() methods
                        response.setVoyageurId(Math.toIntExact(annonce.getVoyageur().getId()));
                        response.setVoyageurNom(annonce.getVoyageur().getNom());
                        response.setVoyageurEmail(annonce.getVoyageur().getEmail());
                    }

                    return response;
                });
    }


    public AnnonceResponse createAnnonce(Annonce annonce, AnnonceRequest annonceRequest, String email, Integer voyageId) {

        // Fetch the Voyageur by ID and ensure they are of the correct type
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Voyageur not found"));

        // Check if the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can create annonces");
        }

        // Fetch the related Voyage
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new RuntimeException("Voyage with ID " + voyageId + " not found"));

        // Ensure the logged-in Voyageur owns the Voyage
        if (!voyage.getVoyageur().equals(utilisateur)) {
            throw new RuntimeException("The Voyage does not belong to the logged-in Voyageur");
        }

        // Associate the Voyageur with the Annonce
        annonce.setVoyageur((Voyageur) utilisateur);

        // Create and populate the Annonce
        annonce.setDatePublication(annonceRequest.getDatePublication());
        annonce.setPoidsDisponible(annonceRequest.getPoidsDisponible());
        annonce.setVoyage(voyage);

        // Save the Annonce
        Annonce savedAnnonce = annonceRepository.save(annonce);

        // Build and return the response DTO
        AnnonceResponse response = new AnnonceResponse();
        response.setId(savedAnnonce.getId());
        response.setDatePublication(savedAnnonce.getDatePublication());
        response.setPoidsDisponible(savedAnnonce.getPoidsDisponible());
        response.setDateDepart(voyage.getDateDepart()); // Get from Voyage
        response.setDateArrivee(voyage.getDateArrivee()); // Get from Voyage
        response.setPaysDepart(voyage.getPaysDepart().getNom()); // Get from Voyage
        response.setPaysDestination(voyage.getPaysDestination().getNom()); // Get from Voyage
        response.setVoyageId(voyage.getId());

        return response;
    }



    public AnnonceResponse updateAnnonce(Integer annonceId, AnnonceRequest annonceRequest, String email) {
        // Fetch the Voyageur by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Voyageur not found"));

        // Check if the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can update annonces");
        }

        // Fetch the existing Annonce
        Annonce existingAnnonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce with ID " + annonceId + " not found"));

        // Ensure the logged-in Voyageur owns the Annonce
        if (!existingAnnonce.getVoyageur().equals(utilisateur)) {
            throw new RuntimeException("The Annonce does not belong to the logged-in Voyageur");
        }

        // Update the Annonce fields
        existingAnnonce.setDatePublication(annonceRequest.getDatePublication());
        existingAnnonce.setPoidsDisponible(annonceRequest.getPoidsDisponible());

        // Save the updated Annonce
        Annonce updatedAnnonce = annonceRepository.save(existingAnnonce);

        // Build and return the response DTO
        AnnonceResponse response = new AnnonceResponse();
        response.setId(updatedAnnonce.getId());
        response.setDatePublication(updatedAnnonce.getDatePublication());
        response.setPoidsDisponible(updatedAnnonce.getPoidsDisponible());
        response.setDateDepart(updatedAnnonce.getVoyage().getDateDepart()); // Get from Voyage
        response.setDateArrivee(updatedAnnonce.getVoyage().getDateArrivee()); // Get from Voyage
        response.setPaysDepart(updatedAnnonce.getVoyage().getPaysDepart().getNom()); // Get from Voyage
        response.setPaysDestination(updatedAnnonce.getVoyage().getPaysDestination().getNom()); // Get from Voyage
        response.setVoyageId(updatedAnnonce.getVoyage().getId());

        return response;

    }

    public void deleteAnnonce(Integer annonceId, String email) {
        // Fetch the Voyageur by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Voyageur not found"));

        // Check if the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can delete annonces");
        }

        // Fetch the existing Annonce
        Annonce existingAnnonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce with ID " + annonceId + " not found"));

        // Ensure the logged-in Voyageur owns the Annonce
        if (!existingAnnonce.getVoyageur().equals(utilisateur)) {
            throw new RuntimeException("The Annonce does not belong to the logged-in Voyageur");
        }

        // Delete the Annonce
        annonceRepository.delete(existingAnnonce);
    }

    public void deleteAnnonces(List<Integer> annonceIds, String email) {
        // Validate user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch and delete specific annonces
        List<Annonce> annonces = annonceRepository.findAllById(annonceIds);
        for (Annonce annonce : annonces) {
            // Ensure the annonces belong to the user
            if (!annonce.getVoyageur().equals(utilisateur)) {
                throw new RuntimeException("You are not authorized to delete this annonce");
            }
        }
        annonceRepository.deleteAll(annonces);
    }

    @Transactional
    public void deleteAllAnnonces(String email) {
        // Fetch the Utilisateur (Voyageur) by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Ensure the logged-in user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can delete annonces");
        }

        // Delete all annonces belonging to the logged-in Voyageur
        List<Annonce> annonces = annonceRepository.findByVoyageur((Voyageur) utilisateur);
        if (annonces.isEmpty()) {
            throw new RuntimeException("No annonces found for the provided user");
        }

        // Delete the annonces
        annonceRepository.deleteAll(annonces);
    }



    public List<Annonce> getAnnoncesByPaysDepart(String paysDepart) {
        return annonceRepository.findByVoyagePaysDepartNom(paysDepart);
    }

    public List<Annonce> getAnnoncesByPaysDestination(String paysDestinationNom) {
        return annonceRepository.findByVoyagePaysDestinationNom(paysDestinationNom);
    }



    public List<Annonce> getFilteredAnnonces(Filtre filtre) {
        // Fetch all annonces with voyages included
        List<Annonce> annoncesWithVoyage = annonceRepository.findAllWithVoyage();

        // Apply filters to the result list
        return annoncesWithVoyage.stream()
                .filter(annonce ->
                        // Filter by dateDepart
                        Optional.ofNullable(filtre.getDateDepart())
                                .map(dateDepart -> Optional.ofNullable(annonce.getVoyage())
                                        .map(voyage -> !voyage.getDateDepart().before(dateDepart)) // Voyage dateDepart >= filtre dateDepart
                                        .orElse(false))
                                .orElse(true))
                .filter(annonce ->
                        // Filter by dateArrivee
                        Optional.ofNullable(filtre.getDateArrivee())
                                .map(dateArrivee -> Optional.ofNullable(annonce.getVoyage())
                                        .map(voyage -> !voyage.getDateArrivee().after(dateArrivee)) // Voyage dateArrivee <= filtre dateArrivee
                                        .orElse(false))
                                .orElse(true))
                .filter(annonce ->
                        // Filter by paysDepart
                        Optional.ofNullable(filtre.getPaysDepart())
                                .map(paysDepart -> Optional.ofNullable(annonce.getVoyage())
                                        .map(voyage -> voyage.getPaysDepart() != null && voyage.getPaysDepart().getNom().equalsIgnoreCase(paysDepart)) // Matches by paysDepart name
                                        .orElse(false))
                                .orElse(true))
                .filter(annonce ->
                        // Filter by paysDestination
                        Optional.ofNullable(filtre.getPaysDestination())
                                .map(paysDestination -> Optional.ofNullable(annonce.getVoyage())
                                        .map(voyage -> voyage.getPaysDestination() != null && voyage.getPaysDestination().getNom().equalsIgnoreCase(paysDestination)) // Matches by paysDestination name
                                        .orElse(false))
                                .orElse(true))
                .collect(Collectors.toList());
    }



}

