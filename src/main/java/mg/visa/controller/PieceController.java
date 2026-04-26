package mg.visa.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.service.PieceService;
import mg.visa.service.DossierService;
import mg.visa.entity.DossierPieceCommune;
import mg.visa.entity.DossierPieceComplementaire;

@RestController
@RequestMapping("/api/dossiers/{dossierId}/pieces")
public class PieceController {

    private final PieceService pieceService;
    private final DossierService dossierService;

    public PieceController(PieceService pieceService, DossierService dossierService) {
        this.pieceService = pieceService;
        this.dossierService = dossierService;
    }
    
    @PostMapping(path = "/communes/create", consumes = "application/json")
    public ResponseEntity<?> createCommunes(@PathVariable Long dossierId,
                                            @RequestBody List<Long> cataloguePieceIds) {
        try {
            List<DossierPieceCommune> created = dossierService.ajouterPieceCommuneMultiple(dossierId, cataloguePieceIds);
            return ResponseEntity.ok().body(Map.of("created", created));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }

    @PostMapping(path = "/complementaires/create", consumes = "application/json")
    public ResponseEntity<?> createComplementaires(@PathVariable Long dossierId,
                                                   @RequestBody List<Long> cataloguePieceIds) {
        try {
            List<DossierPieceComplementaire> created = dossierService.ajouterPieceComplementaireMultiple(dossierId, cataloguePieceIds);
            return ResponseEntity.ok().body(Map.of("created", created));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }

    @PostMapping("/communes/{pieceId}/upload")
    public ResponseEntity<?> uploadCommune(@PathVariable Long dossierId,
                                           @PathVariable Long pieceId,
                                           @RequestParam("file") MultipartFile file) {
        String path = pieceService.uploadPieceCommune(dossierId, pieceId, file);
        return ResponseEntity.ok().body(java.util.Map.of("path", path));
    }

    @PostMapping("/communes")
    public ResponseEntity<?> uploadCommunes(@PathVariable Long dossierId,
                                            @RequestParam("files") List<MultipartFile> files,
                                            @RequestParam("cataloguePieceIds") List<Long> cataloguePieceIds) {
        if (files.size() != cataloguePieceIds.size()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Le nombre de fichiers doit correspondre au nombre d'IDs de pièces"));
        }
        // create dossier piece entries (will verify completeness and rollback on failure)
        List<DossierPieceCommune> created = dossierService.ajouterPieceCommuneMultiple(dossierId, cataloguePieceIds);

        List<String> paths = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            DossierPieceCommune dpc = created.get(i);
            String path = pieceService.uploadPieceCommune(dossierId, dpc.getId(), file);
            paths.add(path);
        }

        return ResponseEntity.ok().body(Map.of("paths", paths));
    }

    @PostMapping("/complementaires/{pieceId}/upload")
    public ResponseEntity<?> uploadComplementaire(@PathVariable Long dossierId,
                                                  @PathVariable Long pieceId,
                                                  @RequestParam("file") MultipartFile file) {
        String path = pieceService.uploadPieceComplementaire(dossierId, pieceId, file);
        return ResponseEntity.ok().body(Map.of("path", path));
    }

    @PostMapping("/complementaires")
    public ResponseEntity<?> uploadComplementaires(@PathVariable Long dossierId,
                                                    @RequestParam("files") List<MultipartFile> files,
                                                    @RequestParam("cataloguePieceIds") List<Long> cataloguePieceIds) {
        if (files.size() != cataloguePieceIds.size()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le nombre de fichiers doit correspondre au nombre d'IDs de pièces"));
        }

        List<DossierPieceComplementaire> created = dossierService.ajouterPieceComplementaireMultiple(dossierId, cataloguePieceIds);

        List<String> paths = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            DossierPieceComplementaire dpc = created.get(i);
            String path = pieceService.uploadPieceComplementaire(dossierId, dpc.getId(), file);
            paths.add(path);
        }

        return ResponseEntity.ok().body(Map.of("paths", paths));
    }

    @GetMapping
    public ResponseEntity<?> listPieces(@PathVariable Long dossierId) {
        try {
            return ResponseEntity.ok(pieceService.getPiecesByDossier(dossierId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
