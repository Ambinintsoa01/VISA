package mg.visa.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.entity.Demande;
import mg.visa.entity.Demandeur;
import mg.visa.repository.DemandeRepository;
import mg.visa.repository.DemandeurRepository;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;

    public DemandeController(DemandeRepository demandeRepository, DemandeurRepository demandeurRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody(required = false) Map<String, Object> body) {
        Long demandeurId = null;
        if (body != null && body.get("demandeurId") != null) {
            try { demandeurId = Long.valueOf(body.get("demandeurId").toString()); } catch (NumberFormatException e) { /* ignored */ }
        }
        if (demandeurId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "demandeurId requis"));
        }

        Demandeur demandeur = demandeurRepository.findById(demandeurId).orElse(null);
        if (demandeur == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "demandeur introuvable"));
        }

        Demande d = new Demande();
        d.setDemandeur(demandeur);
        Demande saved = demandeRepository.save(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
