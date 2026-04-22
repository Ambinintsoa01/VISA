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
import com.s5.framework.dev.models.Personne;
import com.s5.framework.dev.services.PersonneService;

@Controller
public class PersonneRestController {

    private final PersonneService personneService = new PersonneService();

    @GetMapping("/api/personnes")
    @ResponseBody
    public ResponseEntity<List<Personne>> getAll() {
        return ResponseEntity.ok(personneService.findAll());
    }

    @GetMapping("/api/personnes/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personneService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @PostMapping("/api/personnes")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Personne payload) {
        try {
            Personne saved = personneService.create(payload);
            return ResponseEntity.created(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PutMapping("/api/personnes/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Personne payload) {
        try {
            return ResponseEntity.ok(personneService.update(id, payload));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Personne introuvable")) {
                return ResponseEntity.notFound(error(e.getMessage()));
            }
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @DeleteMapping("/api/personnes/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            personneService.delete(id);
            return ResponseEntity.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound(error(e.getMessage()));
        }
    }

    @GetMapping("/api/personnes/search")
    @ResponseBody
    public ResponseEntity<List<Personne>> search(
            @RequestParam(name = "nom") String nom,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "numVisa") String numVisa,
            @RequestParam(name = "numCarte") String numCarte
    ) {
        return ResponseEntity.ok(personneService.search(nom, email, numVisa, numCarte));
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
