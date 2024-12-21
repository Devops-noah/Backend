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

    public Optional<Voyage> getVoyageById(Long id) {
        return voyageRepository.findById(id);
    }


    public Voyage createVoyage(Voyage voyage, Integer destinationId, String email) {
        // Find the Logged-in user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        // Ensure the user is a Voyageur
        if (!(utilisateur instanceof Voyageur)) {
            throw new RuntimeException("Only Voyageurs can create voyages");
        }

        // Fetch the existing Pays instance by ID
        Pays existingPays= paysRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Pays with ID " + destinationId + " not found"));

        System.out.println("existingPays: " + existingPays);

        // Set the existing Pays as the destination of the Voyage
        voyage.setDestination(existingPays);

        // Set the logged-in Voyageur as the owner of the Voyage
        voyage.setVoyageur(utilisateur);

        // Save the voyage
        return voyageRepository.save(voyage);
    }


}

