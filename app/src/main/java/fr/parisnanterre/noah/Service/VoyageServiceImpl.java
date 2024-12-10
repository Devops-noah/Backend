// Voyage service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Repository.PaysRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoyageServiceImpl {

    private final VoyageRepository voyageRepository;
    private final PaysRepository paysRepository;
    @Autowired
    public VoyageServiceImpl(VoyageRepository voyageRepository, PaysRepository paysRepository) {
        this.voyageRepository = voyageRepository;
        this.paysRepository = paysRepository;
    }

    public List<Voyage> getAllVoyages() {
        return voyageRepository.findAll();
    }

    public Optional<Voyage> getVoyageById(Long id) {
        return voyageRepository.findById(id);
    }


    public Voyage createVoyage(Voyage voyage, Integer destinationId) {
        // Fetch the existing Pays instance by ID
        Pays existingPays = paysRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Pays with ID " + destinationId + " not found"));
        System.out.println("existingPays: " + existingPays);

        // Set the existing Pays as the destination of the Voyage
        voyage.setDestination(existingPays);

        // Save and return the Voyage
        return voyageRepository.save(voyage);
    }

//    public Voyage updateVoyage(Long id, Voyage voyageDetails) {
//        return voyageRepository.findById(id)
//                .map(voyage -> {
//                    voyage.setDateDepart(voyageDetails.getDateDepart());
//                    voyage.setDateArrivee(voyageDetails.getDateArrivee());
//                    voyage.setDestination(voyageDetails.getDestination());
//                    voyage.setPoidsDisponible(voyageDetails.getPoidsDisponible());
//                    voyage.setVoyageur(voyageDetails.getVoyageur());
//                    return voyageRepository.save(voyage);
//                })
//                .orElseThrow(() -> new RuntimeException("Voyage not found"));
//    }

    public void deleteVoyage(Long id) {
        voyageRepository.deleteById(id);
    }

    public List<Voyage> getVoyagesByVoyageur(Utilisateur voyageur) {
        return voyageRepository.findByVoyageur(voyageur);
    }

    public List<Voyage> getVoyagesByDestination(Pays destination) {
        return voyageRepository.findByDestination(destination);
    }
}

