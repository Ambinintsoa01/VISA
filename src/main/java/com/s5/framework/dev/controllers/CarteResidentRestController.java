package com.s5.framework.dev.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maharavo.flame.api.ResponseBody;
import com.maharavo.flame.api.ResponseEntity;
import com.maharavo.flame.stereotype.Controller;
import com.maharavo.flame.web.bind.DeleteMapping;
import com.maharavo.flame.web.bind.GetMapping;
import com.maharavo.flame.web.bind.PathVariable;
import com.maharavo.flame.web.bind.PostMapping;
import com.maharavo.flame.web.bind.PutMapping;
import com.maharavo.flame.web.bind.RequestBody;
import com.maharavo.flame.web.bind.RequestParam;
import com.s5.framework.dev.models.CarteResident;
import com.s5.framework.dev.services.CarteResidentService;

@Controller
public class CarteResidentRestController {

    private final CarteResidentService carteService = new CarteResidentService();

    @GetMapping("/api/cartes-resident")
    @ResponseBody
    public ResponseEntity<List<CarteResident>> getAll() {
        return ResponseEntity.ok(carteService.findAll());
    }

    @GetMapping("/api/cartes-resident/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(carteService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @GetMapping("/api/cartes-resident/search")
    @ResponseBody
    public ResponseEntity<List<CarteResident>> search(
            @RequestParam(name = "numCarte") String numCarte,
            @RequestParam(name = "idPersonne") Long idPersonne,
            @RequestParam(name = "idStatutCarte") Integer idStatutCarte
    ) {
        return ResponseEntity.ok(carteService.search(numCarte, idPersonne, idStatutCarte));
    }

    @GetMapping("/api/cartes-resident/personne/{idPersonne}/historique")
    @ResponseBody
    public ResponseEntity<List<CarteResident>> historiqueByPersonne(@PathVariable Long idPersonne) {
        return ResponseEntity.ok(carteService.findHistoryByPersonne(idPersonne));
    }

    @GetMapping("/api/cartes-resident/expirant")
    @ResponseBody
    public ResponseEntity<List<CarteResident>> expirant(
            @RequestParam(name = "jours") Integer jours
    ) {
        return ResponseEntity.ok(carteService.findExpiringWithinDays(jours));
    }

    @PostMapping("/api/cartes-resident")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody CarteResident payload) {
        try {
            CarteResident saved = carteService.create(payload);
            return ResponseEntity.created(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/cartes-resident/generation-transformation")
    @ResponseBody
    public ResponseEntity<?> genererApresTransformation(@RequestBody CarteResident payload) {
        try {
            return ResponseEntity.created(carteService.genererApresTransformation(payload));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/cartes-resident/{idCarteOriginal}/duplicata")
    @ResponseBody
    public ResponseEntity<?> creerDuplicata(
            @PathVariable Long idCarteOriginal,
            @RequestBody CarteResident payload,
            @RequestParam(name = "idDemande") Long idDemande,
            @RequestParam(name = "motif") String motif
    ) {
        try {
            return ResponseEntity.created(carteService.creerDuplicata(idCarteOriginal, idDemande, payload, motif));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/cartes-resident/{idCarteOriginal}/transfert")
    @ResponseBody
    public ResponseEntity<?> transferer(
            @PathVariable Long idCarteOriginal,
            @RequestBody CarteResident nouveauPayload,
            @RequestParam(name = "idDemande") Long idDemande,
            @RequestParam(name = "motif") String motif
    ) {
        try {
            return ResponseEntity.created(carteService.transferer(idCarteOriginal, nouveauPayload, idDemande, motif));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/cartes-resident/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CarteResident payload) {
        try {
            return ResponseEntity.ok(carteService.update(id, payload));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Carte resident introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/cartes-resident/{id}/statut")
    @ResponseBody
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestParam(name = "codeStatut") String codeStatut
    ) {
        try {
            return ResponseEntity.ok(carteService.changerStatut(id, codeStatut));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Carte resident introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @DeleteMapping("/api/cartes-resident/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            carteService.delete(id);
            return ResponseEntity.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
