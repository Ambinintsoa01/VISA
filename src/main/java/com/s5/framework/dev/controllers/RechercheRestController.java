package com.s5.framework.dev.controllers;

import java.util.HashMap;
import java.util.Map;

import com.maharavo.flame.api.ResponseBody;
import com.maharavo.flame.api.ResponseEntity;
import com.maharavo.flame.stereotype.Controller;
import com.maharavo.flame.web.bind.GetMapping;
import com.maharavo.flame.web.bind.RequestParam;
import com.s5.framework.dev.models.MultiCritereSearchResult;
import com.s5.framework.dev.services.MultiCritereSearchService;

@Controller
public class RechercheRestController {

    private final MultiCritereSearchService rechercheService = new MultiCritereSearchService();

    @GetMapping("/api/recherche/multi-criteres")
    @ResponseBody
    public ResponseEntity<?> searchMultiCriteres(
            @RequestParam(name = "nom") String nom,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "numVisa") String numVisa,
            @RequestParam(name = "numCarte") String numCarte,
            @RequestParam(name = "idPersonne") Long idPersonne,
            @RequestParam(name = "idStatutVisa") Integer idStatutVisa,
            @RequestParam(name = "idStatutDemande") Integer idStatutDemande,
            @RequestParam(name = "idTypeDemande") Integer idTypeDemande,
            @RequestParam(name = "idStatutCarte") Integer idStatutCarte,
            @RequestParam(name = "sansDonneInterieur") Boolean sansDonneInterieur
    ) {
        try {
            MultiCritereSearchResult result = rechercheService.search(
                    nom,
                    email,
                    numVisa,
                    numCarte,
                    idPersonne,
                    idStatutVisa,
                    idStatutDemande,
                    idTypeDemande,
                    idStatutCarte,
                    sansDonneInterieur
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
