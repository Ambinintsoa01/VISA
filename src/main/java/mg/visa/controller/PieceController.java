package mg.visa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mg.visa.service.PieceService;

@RestController
@RequestMapping("/api/dossiers/{dossierId}/pieces")
public class PieceController {

    private final PieceService pieceService;

    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @PostMapping("/communes/{pieceId}/upload")
    public ResponseEntity<?> uploadCommune(@PathVariable Long dossierId,
                                           @PathVariable Long pieceId,
                                           @RequestParam("file") MultipartFile file) {
        String path = pieceService.uploadPieceCommune(dossierId, pieceId, file);
        return ResponseEntity.ok().body(java.util.Map.of("path", path));
    }

    @PostMapping("/complementaires/{pieceId}/upload")
    public ResponseEntity<?> uploadComplementaire(@PathVariable Long dossierId,
                                                  @PathVariable Long pieceId,
                                                  @RequestParam("file") MultipartFile file) {
        String path = pieceService.uploadPieceComplementaire(dossierId, pieceId, file);
        return ResponseEntity.ok().body(java.util.Map.of("path", path));
    }

    @GetMapping
    public ResponseEntity<?> listPieces(@PathVariable Long dossierId) {
        try {
            return ResponseEntity.ok(pieceService.getPiecesByDossier(dossierId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
