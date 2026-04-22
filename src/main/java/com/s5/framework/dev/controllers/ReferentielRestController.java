package com.s5.framework.dev.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maharavo.flame.api.ResponseBody;
import com.maharavo.flame.api.ResponseEntity;
import com.maharavo.flame.stereotype.Controller;
import com.maharavo.flame.web.bind.GetMapping;
import com.maharavo.flame.web.bind.PathVariable;
import com.maharavo.flame.web.bind.PostMapping;
import com.maharavo.flame.web.bind.RequestBody;
import com.maharavo.flame.web.bind.RequestParam;
import com.s5.framework.dev.models.ReferentielItem;
import com.s5.framework.dev.services.ReferentielService;

@Controller
public class ReferentielRestController {

    private final ReferentielService referentielService = new ReferentielService();

    @GetMapping("/api/referentiels")
    @ResponseBody
    public ResponseEntity<Map<String, List<ReferentielItem>>> getAllActifs() {
        return ResponseEntity.ok(referentielService.getAllReferentielsActifs());
    }

    @GetMapping("/api/referentiels/{type}")
    @ResponseBody
    public ResponseEntity<?> getByType(
            @PathVariable String type,
            @RequestParam(name = "actifOnly") Boolean actifOnly
    ) {
        try {
            return ResponseEntity.ok(referentielService.getByType(type, actifOnly));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(error(e.getMessage()));
        }
    }

    @PostMapping("/api/referentiels/{type}")
    @ResponseBody
    public ResponseEntity<?> create(
            @PathVariable String type,
            @RequestBody ReferentielItem payload
    ) {
        try {
            return ResponseEntity.created(referentielService.create(type, payload));
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
