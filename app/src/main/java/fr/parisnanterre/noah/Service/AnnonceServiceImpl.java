// Annonce service
package fr.parisnanterre.noah.Service;

//import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.Filtre;
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


    private AnnonceRepository annonceRepository;
    private PaysRepository paysRepository;
    private VoyageRepository voyageRepository;
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    public AnnonceServiceImpl(AnnonceRepository annonceRepository, PaysRepository paysRepository, VoyageRepository voyageRepository) {
        this.annonceRepository = annonceRepository;
        this.paysRepository = paysRepository;
        this.voyageRepository = voyageRepository;
    }

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public Optional<Annonce> getAnnonceById(Integer id) {
        return annonceRepository.findById(id);
    }

    public void createAnnonce(Annonce annonce, Long voyageId, String paysDepartNom, String paysDestinationNom) {
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



//    public Annonce updateAnnonce(Integer id, AnnonceRequest annonceRequest) {
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



//    public List<Annonce> getFilteredAnnonces(Filtre filtre) {
//        // Fetch all annonces with voyages included
//        List<Annonce> annoncesWithVoyage = annonceRepository.findAllWithVoyage();
//
//        // Apply filters to the result list
//        return annoncesWithVoyage.stream()
//                .filter(annonce ->
//                        Optional.ofNullable(filtre.getDateDepart())
//                                .map(dateDepart -> Optional.ofNullable(annonce.getVoyage())
//                                        .map(voyage -> !voyage.getDateDepart().before(dateDepart)) // Assuming you want to compare with voyage's dateDepart
//                                        .orElse(false))
//                                .orElse(true))
//                .filter(annonce ->
//                        Optional.ofNullable(filtre.getPrixMax())
//                                .map(prixMax -> annonce.getPrix() <= prixMax)
//                                .orElse(true))
//                .filter(annonce ->
//                        Optional.ofNullable(filtre.getPoidsMin())
//                                .map(poidsMin -> annonce.getPoids() >= poidsMin)
//                                .orElse(true))
//                .filter(annonce ->
//                        Optional.ofNullable(filtre.getDestinationNom())
//                                .map(destinationNom -> Optional.ofNullable(annonce.getPaysDestination())
//                                        .map(paysDestination -> paysDestination.getNom().equalsIgnoreCase(destinationNom)) // Checks by destination name
//                                        .orElse(false))
//                                .orElse(true))
//                .collect(Collectors.toList());
//    }



}

