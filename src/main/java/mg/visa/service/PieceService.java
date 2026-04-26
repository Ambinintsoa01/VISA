package mg.visa.service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import mg.visa.entity.Dossier;
import mg.visa.entity.DossierPieceCommune;
import mg.visa.entity.DossierPieceComplementaire;
import mg.visa.entity.ref.StatutPiece;
import mg.visa.repository.DossierPieceCommuneRepository;
import mg.visa.repository.DossierPieceComplementaireRepository;
import mg.visa.repository.DossierRepository;
import mg.visa.repository.ref.StatutPieceRepository;

@Service
public class PieceService {

    private final FileStorageService fileStorageService;
    private final DossierRepository dossierRepository;
    private final DossierPieceCommuneRepository dossierPieceCommuneRepository;
    private final DossierPieceComplementaireRepository dossierPieceComplementaireRepository;
    private final StatutPieceRepository statutPieceRepository;

    public PieceService(FileStorageService fileStorageService,
                        DossierRepository dossierRepository,
                        DossierPieceCommuneRepository dossierPieceCommuneRepository,
                        DossierPieceComplementaireRepository dossierPieceComplementaireRepository,
                        StatutPieceRepository statutPieceRepository) {
        this.fileStorageService = fileStorageService;
        this.dossierRepository = dossierRepository;
        this.dossierPieceCommuneRepository = dossierPieceCommuneRepository;
        this.dossierPieceComplementaireRepository = dossierPieceComplementaireRepository;
        this.statutPieceRepository = statutPieceRepository;
    }

    @Transactional
    public String uploadPieceCommune(Long dossierId, Long pieceId, MultipartFile file) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new IllegalArgumentException("Dossier introuvable"));

        DossierPieceCommune dpc = dossierPieceCommuneRepository.findById(pieceId)
                .orElseThrow(() -> new IllegalArgumentException("Piece commune introuvable"));
        if (!dpc.getDossier().getId().equals(dossier.getId())) {
            throw new IllegalArgumentException("La piece ne correspond pas au dossier");
        }

        String path = fileStorageService.store(file);

        StatutPiece fourni = statutPieceRepository.findByCode("FOURNI");
        dpc.setFichierPath(path);
        dpc.setDateFourni(OffsetDateTime.now());
        if (fourni != null) dpc.setStatutPiece(fourni);

        dossierPieceCommuneRepository.save(dpc);
        return path;
    }

    @Transactional
    public String uploadPieceComplementaire(Long dossierId, Long pieceId, MultipartFile file) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new IllegalArgumentException("Dossier introuvable"));

        DossierPieceComplementaire dpc = dossierPieceComplementaireRepository.findById(pieceId)
                .orElseThrow(() -> new IllegalArgumentException("Piece complementaire introuvable"));
        if (!dpc.getDossier().getId().equals(dossier.getId())) {
            throw new IllegalArgumentException("La piece ne correspond pas au dossier");
        }

        String path = fileStorageService.store(file);

        StatutPiece fourni = statutPieceRepository.findByCode("FOURNI");
        dpc.setFichierPath(path);
        dpc.setDateFourni(OffsetDateTime.now());
        if (fourni != null) dpc.setStatutPiece(fourni);

        dossierPieceComplementaireRepository.save(dpc);
        return path;
    }

    public Map<String, Object> getPiecesByDossier(Long dossierId) {
        // verify dossier exists
        dossierRepository.findById(dossierId).orElseThrow(() -> new IllegalArgumentException("Dossier introuvable"));

        List<DossierPieceCommune> communes = dossierPieceCommuneRepository.findByDossierId(dossierId);
        List<DossierPieceComplementaire> comps = dossierPieceComplementaireRepository.findByDossierId(dossierId);

        Map<String, Object> result = new HashMap<>();
        result.put("communes", communes);
        result.put("complementaires", comps);
        return result;
    }
}
