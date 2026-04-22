package mg.visa.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.dto.DossierCreationDTO;
import mg.visa.dto.DossierCreationResult;
import mg.visa.dto.StatusChangeDTO;
import mg.visa.entity.Dossier;
import mg.visa.exception.MissingPiecesException;
import mg.visa.service.DossierService;

@RestController
@RequestMapping("/api/dossiers")
public class DossierController {

    private final DossierService dossierService;

    public DossierController(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DossierCreationDTO dto) {
        try {
            DossierCreationResult result = dossierService.creerDossier(dto);
            Map<String, Object> body = java.util.Map.of(
                "dossier", result.getDossier()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (MissingPiecesException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("missingPieces", ex.getMissingPieces()));
        }
    }
    
    @GetMapping("/{id}/completude")
    public ResponseEntity<?> completude(@PathVariable("id") Long id) {
        boolean complete = dossierService.verifierCompletude(id);
        return ResponseEntity.ok(java.util.Map.of("completude", complete));
    }
    
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable("id") Long id, @RequestBody StatusChangeDTO dto) {
        if (dto == null || dto.getCode() == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "code requis"));
        }
        Dossier updated = dossierService.changerStatut(id, dto.getCode());
        return ResponseEntity.ok(updated);
    }
}
