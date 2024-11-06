// Pays service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Repository.PaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaysServiceImpl {

    @Autowired
    private PaysRepository paysRepository;

    public List<Pays> getAllPays() {
        return paysRepository.findAll();
    }

    public Optional<Pays> getPaysById(Integer id) {
        return paysRepository.findById(id);
    }

    public Pays createPays(Pays pays) {
        return paysRepository.save(pays);
    }

    public Pays updatePays(Integer id, Pays paysDetails) {
        return paysRepository.findById(id)
                .map(pays -> {
                    pays.setNom(paysDetails.getNom());
                    pays.setCodeISO(paysDetails.getCodeISO());
                    pays.setVille(paysDetails.getVille());
                    return paysRepository.save(pays);
                }).orElseThrow(() -> new RuntimeException("Pays not found"));
    }

    public void deletePays(Integer id) {
        paysRepository.deleteById(id);
    }

    public Optional<Pays> getPaysByNom(String nom) {
        return paysRepository.findByNom(nom);
    }



}

