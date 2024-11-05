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

    public Pays createPays(Pays pays) {
        return paysRepository.save(pays);
    }

}

