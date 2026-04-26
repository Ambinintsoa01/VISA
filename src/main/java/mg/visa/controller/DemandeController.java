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
import mg.visa.entity.Passeport;
import mg.visa.entity.VisaTransformable;
import mg.visa.entity.ref.TypeDemande;
import mg.visa.repository.DemandeRepository;
import mg.visa.repository.DemandeurRepository;
import mg.visa.repository.PasseportRepository;
import mg.visa.repository.VisaTransformableRepository;
import mg.visa.repository.ref.TypeDemandeRepository;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final VisaTransformableRepository visaTransformableRepository;

    public DemandeController(DemandeRepository demandeRepository, DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository, TypeDemandeRepository typeDemandeRepository,
            VisaTransformableRepository visaTransformableRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.visaTransformableRepository = visaTransformableRepository;
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

        Long passeportId = null;
        if (body != null && body.get("passeportId") != null) {
            try { passeportId = Long.valueOf(body.get("passeportId").toString()); } catch (NumberFormatException e) { /* ignored */ }
        }
        if (passeportId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "passeportId requis"));
        }

        Long typeDemandeId = null;
        if (body != null && body.get("typeDemandeId") != null) {
            try { typeDemandeId = Long.valueOf(body.get("typeDemandeId").toString()); } catch (NumberFormatException e) { /* ignored */ }
        }
        if (typeDemandeId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "typeDemandeId requis"));
        }

        Demandeur demandeur = demandeurRepository.findById(demandeurId).orElse(null);
        if (demandeur == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "demandeur introuvable"));
        }

        Passeport passeport = passeportRepository.findById(passeportId).orElse(null);
        if (passeport == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "passeport introuvable"));
        }

        TypeDemande typeDemande = typeDemandeRepository.findById(typeDemandeId).orElse(null);
        if (typeDemande == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "typeDemande introuvable"));
        }

        Long idVisaTransformable = null;
        if (body != null && body.get("idVisaTransformable") != null) {
            try { idVisaTransformable = Long.valueOf(body.get("idVisaTransformable").toString()); } catch (NumberFormatException e) { /* ignored */ }
        }

        VisaTransformable vt = null;
        if (idVisaTransformable != null) {
            vt = visaTransformableRepository.findById(idVisaTransformable).orElse(null);
            if (vt == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "visaTransformable introuvable"));
            }
        }

        Demande d = new Demande();
        d.setDemandeur(demandeur);
        d.setPasseport(passeport);
        d.setTypeDemande(typeDemande);
        if (vt != null) d.setVisaTransformable(vt);
        Demande saved = demandeRepository.save(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
