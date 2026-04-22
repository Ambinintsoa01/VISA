package mg.visa.service;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.dto.DossierCreationDTO;
import mg.visa.entity.CataloguePieceCommune;
import mg.visa.entity.CataloguePieceComplementaire;
import mg.visa.entity.Demande;
import mg.visa.entity.Dossier;
import mg.visa.entity.DossierPieceCommune;
import mg.visa.entity.DossierPieceComplementaire;
import mg.visa.entity.ref.StatutDossier;
import mg.visa.entity.ref.StatutPiece;
import mg.visa.repository.CataloguePieceCommuneRepository;
import mg.visa.repository.CataloguePieceComplementaireRepository;
import mg.visa.repository.DemandeRepository;
import mg.visa.repository.DossierPieceCommuneRepository;
import mg.visa.repository.DossierPieceComplementaireRepository;
import mg.visa.repository.DossierRepository;
import mg.visa.repository.ref.StatutDossierRepository;
import mg.visa.repository.ref.StatutPieceRepository;

@Service
public class DossierService {

    private final DossierRepository dossierRepository;
    private final DemandeRepository demandeRepository;
    private final StatutDossierRepository statutDossierRepository;
    private final StatutPieceRepository statutPieceRepository;
    private final CataloguePieceCommuneRepository cataloguePieceCommuneRepository;
    private final CataloguePieceComplementaireRepository cataloguePieceComplementaireRepository;
    private final DossierPieceCommuneRepository dossierPieceCommuneRepository;
    private final DossierPieceComplementaireRepository dossierPieceComplementaireRepository;

    public DossierService(DossierRepository dossierRepository,
                         DemandeRepository demandeRepository,
                         StatutDossierRepository statutDossierRepository,
                         StatutPieceRepository statutPieceRepository,
                         CataloguePieceCommuneRepository cataloguePieceCommuneRepository,
                         CataloguePieceComplementaireRepository cataloguePieceComplementaireRepository,
                         DossierPieceCommuneRepository dossierPieceCommuneRepository,
                         DossierPieceComplementaireRepository dossierPieceComplementaireRepository) {
        this.dossierRepository = dossierRepository;
        this.demandeRepository = demandeRepository;
        this.statutDossierRepository = statutDossierRepository;
        this.statutPieceRepository = statutPieceRepository;
        this.cataloguePieceCommuneRepository = cataloguePieceCommuneRepository;
        this.cataloguePieceComplementaireRepository = cataloguePieceComplementaireRepository;
        this.dossierPieceCommuneRepository = dossierPieceCommuneRepository;
        this.dossierPieceComplementaireRepository = dossierPieceComplementaireRepository;
    }

    @Transactional
    public Dossier creerDossier(DossierCreationDTO dto) {
        if (dto.getDemandeId() == null) throw new ResponseStatusException(BAD_REQUEST, "demandeId requis");

        Demande demande = demandeRepository.findById(dto.getDemandeId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "demande introuvable"));

        Dossier dossier = new Dossier();
        dossier.setDemande(demande);
        dossier.setNumeroDossier("D-" + System.currentTimeMillis());
        dossier.setCreatedBy(dto.getCreatedBy());

        // statut dossier par défaut
        StatutDossier sd = statutDossierRepository.findByCode("OPEN").orElse(null);
        dossier.setStatutDossier(sd);

        Dossier saved = dossierRepository.save(dossier);

        // initialiser pièces communes
        List<CataloguePieceCommune> communes = cataloguePieceCommuneRepository.findAll();
        StatutPiece nonFourni = statutPieceRepository.findByCode("NON_FOURNI").orElse(null);

        for (CataloguePieceCommune c : communes) {
            DossierPieceCommune dpc = new DossierPieceCommune();
            dpc.setDossier(saved);
            dpc.setCataloguePieceCommune(c);
            dpc.setStatutPiece(nonFourni);
            dossierPieceCommuneRepository.save(dpc);
        }

        // initialiser pièces complémentaires
        List<CataloguePieceComplementaire> comps = cataloguePieceComplementaireRepository.findAll();
        for (CataloguePieceComplementaire c : comps) {
            DossierPieceComplementaire dpc = new DossierPieceComplementaire();
            dpc.setDossier(saved);
            dpc.setCataloguePieceComplementaire(c);
            dpc.setStatutPiece(nonFourni);
            dossierPieceComplementaireRepository.save(dpc);
        }

        return saved;
    }

    public boolean verifierCompletude(Long dossierId) {
        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "dossier introuvable"));

        // Check all mandatory catalogue pieces (commune)
        List<DossierPieceCommune> communes = dossierPieceCommuneRepository.findByDossierId(dossierId);
        for (DossierPieceCommune dpc : communes) {
            if (Boolean.TRUE.equals(dpc.getCataloguePieceCommune().getObligatoire())) {
                if (dpc.getStatutPiece() == null || !"FOURNI".equalsIgnoreCase(dpc.getStatutPiece().getCode())) {
                    return false;
                }
            }
        }

        // Check mandatory complementary pieces
        List<DossierPieceComplementaire> comps = dossierPieceComplementaireRepository.findByDossierId(dossierId);
        for (DossierPieceComplementaire dpc : comps) {
            if (Boolean.TRUE.equals(dpc.getCataloguePieceComplementaire().getObligatoire())) {
                if (dpc.getStatutPiece() == null || !"FOURNI".equalsIgnoreCase(dpc.getStatutPiece().getCode())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Transactional
    public Dossier changerStatut(Long dossierId, String statutCode) {
        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "dossier introuvable"));

        if (statutCode == null || statutCode.isBlank()) throw new ResponseStatusException(BAD_REQUEST, "statutCode requis");

        // If approving, ensure dossier is complete
        if (statutCode.equalsIgnoreCase("APPROUVE") || statutCode.equalsIgnoreCase("APPROVED") || statutCode.equalsIgnoreCase("APPROVE")) {
            if (!verifierCompletude(dossierId)) {
                throw new ResponseStatusException(BAD_REQUEST, "dossier incomplet, impossible d'approuver");
            }
        }

        StatutDossier sd = statutDossierRepository.findByCode(statutCode).orElse(null);
        if (sd == null) {
            // create new statut if not exists
            sd = new StatutDossier();
            sd.setCode(statutCode);
            sd.setLibelle(statutCode);
            sd = statutDossierRepository.save(sd);
        }

        dossier.setStatutDossier(sd);
        return dossierRepository.save(dossier);
    }
}
