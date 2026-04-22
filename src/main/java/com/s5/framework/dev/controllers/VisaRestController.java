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
import com.s5.framework.dev.models.Visa;
import com.s5.framework.dev.services.VisaService;

@Controller
public class VisaRestController {

    private final VisaService visaService = new VisaService();

    @GetMapping("/api/visas")
    @ResponseBody
    public ResponseEntity<List<Visa>> getAll() {
        return ResponseEntity.ok(visaService.findAll());
    }

    @GetMapping("/api/visas/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(visaService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @GetMapping("/api/visas/search")
    @ResponseBody
    public ResponseEntity<List<Visa>> search(
            @RequestParam(name = "numVisa") String numVisa,
            @RequestParam(name = "idPersonne") Long idPersonne,
            @RequestParam(name = "idStatutVisa") Integer idStatutVisa
    ) {
        return ResponseEntity.ok(visaService.search(numVisa, idPersonne, idStatutVisa));
    }

    @GetMapping("/api/visas/personne/{idPersonne}/historique")
    @ResponseBody
    public ResponseEntity<List<Visa>> historiqueByPersonne(@PathVariable Long idPersonne) {
        return ResponseEntity.ok(visaService.findHistoryByPersonne(idPersonne));
    }

    @PostMapping("/api/visas")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Visa payload) {
        try {
            Visa saved = visaService.create(payload);
            return ResponseEntity.created(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/visas/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Visa payload) {
        try {
            return ResponseEntity.ok(visaService.update(id, payload));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Visa introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @DeleteMapping("/api/visas/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            visaService.delete(id);
            return ResponseEntity.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @PutMapping("/api/visas/{id}/transformable")
    @ResponseBody
    public ResponseEntity<?> marquerTransformable(@PathVariable Long id, @RequestParam(name = "value") Boolean value) {
        try {
            return ResponseEntity.ok(visaService.marquerTransformable(id, value));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Visa introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/visas/{id}/connu-interieur")
    @ResponseBody
    public ResponseEntity<?> marquerConnuInterieur(@PathVariable Long id, @RequestParam(name = "value") Boolean value) {
        try {
            return ResponseEntity.ok(visaService.marquerConnuInterieur(id, value));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Visa introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/visas/{id}/transfert")
    @ResponseBody
    public ResponseEntity<?> transferer(
            @PathVariable Long id,
            @RequestBody Visa nouveauVisa,
            @RequestParam(name = "motif") String motif
    ) {
        try {
            return ResponseEntity.created(visaService.transferer(id, nouveauVisa, motif));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
