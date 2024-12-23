// Voyage service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.PaysRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public List<Voyage> getAllVoyages() {
        return voyageRepository.findAll();
    }

    public Optional<Voyage> getVoyageById(Integer id) {
        return voyageRepository.findById(id);
    }


    public Voyage createVoyage(Voyage voyage, String paysDepart, String paysDestination, String email) {
        // Find the Logged-in user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        // Ensure the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can create voyages");
        }

        if (paysDepart == null || paysDepart.isEmpty()) {
            throw new RuntimeException("Pays depart is missing");
        }
        // Fetch the existing Pays instance by ID
        Pays paysDepartNom= paysRepository.findByNom(paysDepart)
                .orElseThrow(() -> new RuntimeException("Pays depart with Name " + paysDepart + " not found"));

        System.out.println("paysDepart: " + paysDepart);

        if (paysDestination == null || paysDestination.isEmpty()) {
            throw new RuntimeException("Pays destination is missing");
        }
        Pays paysDestinationNom= paysRepository.findByNom(paysDestination)
                .orElseThrow(() -> new RuntimeException("Pays destination with Name " + paysDestination + " not found"));

        // Set the existing Pays as the destination of the Voyage
        voyage.setPaysDepart(paysDepartNom);
        voyage.setPaysDestination(paysDestinationNom);

        // Set the logged-in Voyageur as the owner of the Voyage
        voyage.setVoyageur(utilisateur);
        System.out.println("voyage value: " + voyage);

        Voyage savedVoyage = voyageRepository.save(voyage);

        // Return the saved voyage with country names
        savedVoyage.setPaysDepart(paysDepartNom);
        savedVoyage.setPaysDestination(paysDestinationNom);
        // Save the voyage
        return savedVoyage;
    }


}

