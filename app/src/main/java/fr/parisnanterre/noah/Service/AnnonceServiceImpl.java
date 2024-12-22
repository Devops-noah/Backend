// Annonce service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.DTO.AnnonceResponseDto;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.PaysRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

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

    public List<AnnonceResponseDto> getAllAnnonces() {
        List<Annonce> annonces = annonceRepository.findAll();

        // Map Annonce to fr.parisnanterre.noah.DTO.AnnonceRequest.AnnonceResponse
        return annonces.stream().map(annonce -> {
            AnnonceResponseDto response = new AnnonceResponseDto();
            response.setId(annonce.getId());
            response.setDateDepart(annonce.getDateDepart());
            response.setDateArrivee(annonce.getDateArrivee());
            response.setDatePublication(annonce.getDatePublication());
            response.setPoidsDisponible(annonce.getPoidsDisponible());
            response.setPaysDepart(annonce.getPaysDepart() != null ? annonce.getPaysDepart().getNom() : null);
            response.setPaysDestination(annonce.getPaysDestination() != null ? annonce.getPaysDestination().getNom() : null);
            response.setVoyageId(String.valueOf(annonce.getVoyage() != null ? annonce.getVoyage().getId() : null));
            return response;
        }).collect(Collectors.toList());
    }

    public Optional<Annonce> getAnnonceById(Integer id) {
        return annonceRepository.findById(id);
    }

    public void createAnnonce(Annonce annonce, Long voyageId, String paysDepartNom, String paysDestinationNom) {
        // Log the incoming Annonce object for debugging
        System.out.println("Annonce in Service: " + annonce);
        System.out.println("Voyage ID: " + voyageId);
        System.out.println("Pays Depart: " + paysDepartNom);
        System.out.println("Pays Destination: " + paysDestinationNom);

        // If Annonce is null or not initialized properly, throw an exception
        if (annonce == null) {
            throw new IllegalArgumentException("Annonce object is null");
        }

        // Check if the voyageId is valid
        if (voyageId == null) {
            throw new IllegalArgumentException("Voyage ID is null");
        }


        // Validate annonce object is not null
        Objects.requireNonNull(annonce, "Annonce object must not be null");

        // Fetch the Voyageur by ID and ensure they are of the correct type
        Utilisateur utilisateur = utilisateurRepository.findById(annonce.getVoyageur().getId())
                .orElseThrow(() -> new RuntimeException("Voyageur not found"));

        // Check if the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can create annonces");
        }

        // Log input data for debugging
        System.out.println("Voyage ID: " + voyageId + ", Pays Depart: " + paysDepartNom + ", Pays Destination: " + paysDestinationNom);

        // Retrieve and set PaysDepart if it exists
        Optional<Pays> paysDepartOpt = Optional.ofNullable(paysDepartNom)
                .flatMap(paysRepository::findByNom);
        paysDepartOpt.ifPresentOrElse(
                annonce::setPaysDepart,
                () -> { throw new RuntimeException("PaysDepart with name " + paysDepartNom + " does not exist."); }
        );

        // Retrieve and set PaysDestination if it exists
        Optional<Pays> paysDestinationOpt = Optional.ofNullable(paysDestinationNom)
                .flatMap(paysRepository::findByNom);
        paysDestinationOpt.ifPresentOrElse(
                annonce::setPaysDestination,
                () -> { throw new RuntimeException("PaysDestination with name " + paysDestinationOpt + " does not exist."); }
        );

        // Retrieve and set Voyage if the ID is provided
        Optional<Voyage> voyageOpt = Optional.ofNullable(voyageId)
                .flatMap(voyageRepository::findById);
        System.out.println("voyage opt: " + voyageOpt);
        voyageOpt.ifPresentOrElse(
                annonce::setVoyage,
                () -> { throw new RuntimeException("Voyage with ID " + voyageId + " does not exist."); }
        );

        // Save the Annonce with existing Pays references
        System.out.println("annonce: " + annonce.getVoyage());
        annonceRepository.save(annonce);
    }



//    public Annonce updateAnnonce(Integer id, fr.parisnanterre.noah.DTO.AnnonceRequest annonceRequest) {
//        return annonceRepository.findById(id)
//                .map(annonce -> {
//                    annonce.setPoids(annonceRequest.getPoids() != null ? annonceRequest.getPoids() : annonce.getPoids());
//                    annonce.setPrix(annonceRequest.getPrix() != null ? annonceRequest.getPrix() : annonce.getPrix());
//                    annonce.setDateCreation(annonce.getDateCreation());
//                    annonce.setExpediteur(annonce.getExpediteur());
//                    annonce.setVoyageur(annonce.getVoyageur());
//
//                    // Update PaysDepart if a new one is provided
//                    Optional<Pays> paysDepartOpt = Optional.ofNullable(annonceRequest.getPaysDepartNom())
//                            .flatMap(paysRepository::findByNom);
//                    paysDepartOpt.ifPresentOrElse(
//                            annonce::setPaysDepart,
//                            () -> { throw new RuntimeException("PaysDepart with name " + annonceRequest.getPaysDepartNom() + " does not exist."); }
//                    );
//
//                    // Update PaysDestination if a new one is provided
//                    Optional<Pays> paysDestinationOpt = Optional.ofNullable(annonceRequest.getPaysDestinationNom())
//                            .flatMap(paysRepository::findByNom);
//                    paysDestinationOpt.ifPresentOrElse(
//                            annonce::setPaysDestination,
//                            () -> { throw new RuntimeException("PaysDestination with name " + annonceRequest.getPaysDestinationNom() + " does not exist."); }
//                    );
//
//                    // Update Voyage if a new one is provided
//                    Optional<Voyage> voyageOpt = Optional.ofNullable(annonceRequest.getVoyageId())
//                            .flatMap(voyageRepository::findById);
//                    voyageOpt.ifPresentOrElse(
//                            annonce::setVoyage,
//                            () -> { throw new RuntimeException("Voyage with ID " + annonceRequest.getVoyageId() + " does not exist."); }
//                    );
//
//                    return annonceRepository.save(annonce);
//                })
//                .orElseThrow(() -> new RuntimeException("Annonce not found"));
//    }

    public void deleteAnnonce(Integer id) {
        annonceRepository.deleteById(id);
    }

    public List<Annonce> getAnnoncesByPaysDepart(Pays paysDepart) {
        return annonceRepository.findByPaysDepart(paysDepart);
    }

    public List<Annonce> getAnnoncesByPaysDestination(Pays paysDest) {
        return annonceRepository.findByPaysDestination(paysDest);
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
                                .map(paysDepart -> Optional.ofNullable(annonce.getPaysDepart())
                                        .map(pays -> pays.getNom().equalsIgnoreCase(paysDepart)) // Matches by paysDepart name
                                        .orElse(false))
                                .orElse(true))
                .filter(annonce ->
                        // Filter by paysDestination
                        Optional.ofNullable(filtre.getPaysDestination())
                                .map(paysDestination -> Optional.ofNullable(annonce.getPaysDestination())
                                        .map(pays -> pays.getNom().equalsIgnoreCase(paysDestination)) // Matches by paysDestination name
                                        .orElse(false))
                                .orElse(true))
                .collect(Collectors.toList());
    }



}

