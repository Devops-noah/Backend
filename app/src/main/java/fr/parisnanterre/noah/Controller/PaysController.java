// Pays.controller
package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Service.PaysServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/pays")
public class PaysController {
    @Autowired
    private PaysServiceImpl paysServiceImpl;

    @GetMapping
    public List<Pays> getAllPays() {
        return paysServiceImpl.getAllPays();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pays> getPaysById(@PathVariable Integer id) {
        return paysServiceImpl.getPaysById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pays createPays(@RequestBody Pays pays) {
        return paysServiceImpl.createPays(pays);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pays> updatePays(@PathVariable Integer id, @RequestBody Pays pays) {
        return ResponseEntity.ok(paysServiceImpl.updatePays(id, pays));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePays(@PathVariable Integer id) {
        paysServiceImpl.deletePays(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nom")
    public ResponseEntity<Optional<Pays>> getPaysByNom(@RequestParam String nom) {
        return ResponseEntity.ok(paysServiceImpl.getPaysByNom(nom));
    }
}
