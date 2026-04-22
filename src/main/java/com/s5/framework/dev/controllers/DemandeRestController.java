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
import com.s5.framework.dev.models.Demande;
import com.s5.framework.dev.services.DemandeService;

@Controller
public class DemandeRestController {

    private final DemandeService demandeService = new DemandeService();

    @GetMapping("/api/demandes")
    @ResponseBody
    public ResponseEntity<List<Demande>> getAll() {
        return ResponseEntity.ok(demandeService.findAll());
    }

    @GetMapping("/api/demandes/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(demandeService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @GetMapping("/api/demandes/search")
    @ResponseBody
    public ResponseEntity<List<Demande>> search(
            @RequestParam(name = "idPersonne") Long idPersonne,
            @RequestParam(name = "idStatutDemande") Integer idStatutDemande,
            @RequestParam(name = "idTypeDemande") Integer idTypeDemande,
            @RequestParam(name = "sansDonneInterieur") Boolean sansDonneInterieur
    ) {
        return ResponseEntity.ok(demandeService.search(idPersonne, idStatutDemande, idTypeDemande, sansDonneInterieur));
    }

    @GetMapping("/api/demandes/en-attente")
    @ResponseBody
    public ResponseEntity<List<Demande>> enAttente() {
        return ResponseEntity.ok(demandeService.findEnAttente());
    }

    @GetMapping("/api/demandes/personne/{idPersonne}/historique")
    @ResponseBody
    public ResponseEntity<List<Demande>> historiqueByPersonne(@PathVariable Long idPersonne) {
        return ResponseEntity.ok(demandeService.findHistoryByPersonne(idPersonne));
    }

    @PostMapping("/api/demandes")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Demande payload) {
        try {
            Demande saved = demandeService.create(payload);
            return ResponseEntity.created(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/demandes/transformation")
    @ResponseBody
    public ResponseEntity<?> createTransformation(@RequestBody Demande payload) {
        try {
            return ResponseEntity.created(demandeService.createTransformation(payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/demandes/duplicata")
    @ResponseBody
    public ResponseEntity<?> createDuplicata(@RequestBody Demande payload) {
        try {
            return ResponseEntity.created(demandeService.createDuplicata(payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/demandes/transfert-visa")
    @ResponseBody
    public ResponseEntity<?> createTransfertVisa(@RequestBody Demande payload) {
        try {
            return ResponseEntity.created(demandeService.createTransfertVisa(payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/demandes/transfert-carte")
    @ResponseBody
    public ResponseEntity<?> createTransfertCarte(@RequestBody Demande payload) {
        try {
            return ResponseEntity.created(demandeService.createTransfertCarte(payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/demandes/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Demande payload) {
        try {
            return ResponseEntity.ok(demandeService.update(id, payload));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Demande introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/demandes/{id}/statut")
    @ResponseBody
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestParam(name = "codeStatut") String codeStatut,
            @RequestParam(name = "traitePar") String traitePar
    ) {
        try {
            return ResponseEntity.ok(demandeService.changerStatut(id, codeStatut, traitePar));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Demande introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @DeleteMapping("/api/demandes/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            demandeService.delete(id);
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
