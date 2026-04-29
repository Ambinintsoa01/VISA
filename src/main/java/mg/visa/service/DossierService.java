package mg.visa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.dto.DossierCreationDTO;
import mg.visa.dto.DossierCreationResult;
import mg.visa.entity.CataloguePieceCommune;
import mg.visa.entity.CataloguePieceComplementaire;
import mg.visa.entity.Demande;
import mg.visa.entity.Dossier;
import mg.visa.entity.DossierPieceCommune;
import mg.visa.entity.DossierPieceComplementaire;
import mg.visa.entity.ref.StatutDossier;
import mg.visa.entity.ref.TypeDemande;
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
    private final DossierPieceCommuneRepository dossierPieceCommuneRepository;
    private final CataloguePieceCommuneRepository cataloguePieceCommuneRepository;
    private final CataloguePieceComplementaireRepository cataloguePieceComplementaireRepository;
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
        this.cataloguePieceCommuneRepository = cataloguePieceCommuneRepository;
        this.cataloguePieceComplementaireRepository = cataloguePieceComplementaireRepository;
        this.dossierPieceCommuneRepository = dossierPieceCommuneRepository;
        this.dossierPieceComplementaireRepository = dossierPieceComplementaireRepository;
    }

    @Transactional
    public DossierCreationResult creerDossier(DossierCreationDTO dto) {
        if (dto.getDemandeId() == null)
            throw new ResponseStatusException(BAD_REQUEST, "demandeId requis");

        Demande demande = demandeRepository.findById(dto.getDemandeId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "demande introuvable"));

        Dossier dossier = new Dossier();
        dossier.setDemande(demande);
        dossier.setNumeroDossier("D-" + System.currentTimeMillis());
        dossier.setCreatedBy(dto.getCreatedBy());

        StatutDossier sd = statutDossierRepository.findByCode("OPEN");
        dossier.setStatutDossier(sd);

        Dossier saved = dossierRepository.save(dossier);

        return new DossierCreationResult(saved, new ArrayList<>());
    }

    public DossierPieceCommune ajouterPieceCommune(Long dossierId, Long cataloguePieceId) {
        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "dossier introuvable"));

        CataloguePieceCommune catPiece = cataloguePieceCommuneRepository.findById(cataloguePieceId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "catalogue piece commune introuvable"));

        DossierPieceCommune dpc = new DossierPieceCommune();
        dpc.setDossier(dossier);
        dpc.setCataloguePieceCommune(catPiece);
        dpc.setStatutPiece(null); // devient FOURNI après upload

        return dossierPieceCommuneRepository.save(dpc);
    }

    @Transactional
    public List<DossierPieceCommune> ajouterPieceCommuneMultiple(Long dossierId,
            List<Long> cataloguePieceIds) {
        List<CataloguePieceCommune> cataloguePieceCommunes = cataloguePieceCommuneRepository
                .findAllById(cataloguePieceIds);
        List<DossierPieceCommune> result = new ArrayList<>();

        // Vérifier la complétude AVANT insertion (sinon rollback partiel)
        if (!verifierCompletudeCommune(cataloguePieceCommunes)) {
            throw new ResponseStatusException(BAD_REQUEST, "Pieces obligatoires incompletes");
        } else {
            for (Long catId : cataloguePieceIds) {
                result.add(ajouterPieceCommune(dossierId, catId));
            }
        }
        return result;
    }

    public boolean verifierCompletudeCommune(List<CataloguePieceCommune> communes) {
        List<CataloguePieceCommune> catalogueCommunes = cataloguePieceCommuneRepository.findAll();
        List<CataloguePieceCommune> obligatoireCommunes = new ArrayList<>();
        for (CataloguePieceCommune cpc : catalogueCommunes) {
            if (Boolean.TRUE.equals(cpc.getObligatoire())) {
                obligatoireCommunes.add(cpc);
            }
        }
        return communes.containsAll(obligatoireCommunes);
    }

    public DossierPieceComplementaire ajouterPieceComplementaire(Long dossierId, Long cataloguePieceId) {
        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "dossier introuvable"));

        CataloguePieceComplementaire catPiece = cataloguePieceComplementaireRepository.findById(cataloguePieceId)
                .orElseThrow(
                        () -> new ResponseStatusException(BAD_REQUEST, "catalogue piece complementaire introuvable"));

        DossierPieceComplementaire dpc = new DossierPieceComplementaire();
        dpc.setDossier(dossier);
        dpc.setCataloguePieceComplementaire(catPiece);
        dpc.setStatutPiece(null); // devient FOURNI après upload

        return dossierPieceComplementaireRepository.save(dpc);
    }

    @Transactional
    public List<DossierPieceComplementaire> ajouterPieceComplementaireMultiple(Long dossierId,
            List<Long> cataloguePieceIds) {
        Dossier dossier = dossierRepository.findById(dossierId).get();
        TypeDemande typeVisa = dossier.getDemande().getTypeDemande();

        List<CataloguePieceComplementaire> cataloguePieceComplementaires = cataloguePieceComplementaireRepository
                .findAllById(cataloguePieceIds);
        List<DossierPieceComplementaire> result = new ArrayList<>();

        // Vérifier la complétude AVANT insertion (sinon rollback partiel)
        if (!verifierCompletudeComplementaire(cataloguePieceComplementaires, typeVisa.getCode())) {
            throw new ResponseStatusException(BAD_REQUEST, "Pieces obligatoires incompletes");
        } else {
            for (Long catId : cataloguePieceIds) {
                result.add(ajouterPieceComplementaire(dossierId, catId));
            }
        }
        return result;
    }

    public boolean verifierCompletudeComplementaire(List<CataloguePieceComplementaire> complementaires,
            String typeVisaCode) {
        List<CataloguePieceComplementaire> catalogueComplementaire = cataloguePieceComplementaireRepository
                .findByTypeVisaCode(typeVisaCode);
        List<CataloguePieceComplementaire> obligatoireComplementaires = new ArrayList<>();
        for (CataloguePieceComplementaire cpc : catalogueComplementaire) {
            if (Boolean.TRUE.equals(cpc.getObligatoire())) {
                obligatoireComplementaires.add(cpc);
            }
        }
        return complementaires.containsAll(obligatoireComplementaires);
    }

    public void validationCreationDossier(DossierCreationDTO dto) {
        if (dto.getDemandeId() == null)
            throw new ResponseStatusException(BAD_REQUEST, "demandeId requis");
        if (!this.verifierCompletude(dto.getDemandeId()))
            throw new ResponseStatusException(BAD_REQUEST, "dossier incomplet, pieces manquantes");
    }

    public boolean verifierCompletude(Long dossierId) {
        // Verify that all catalogue-defined mandatory commune pieces are provided with
        // statut FOURNI
        List<CataloguePieceCommune> catalogueCommunes = cataloguePieceCommuneRepository.findAll();
        if (catalogueCommunes != null && !catalogueCommunes.isEmpty()) {
            Set<Long> requiredCommuneIds = catalogueCommunes.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getObligatoire()))
                    .map(CataloguePieceCommune::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!requiredCommuneIds.isEmpty()) {
                List<DossierPieceCommune> communes = dossierPieceCommuneRepository.findByDossierId(dossierId);
                Set<Long> providedCommuneIds = communes == null ? java.util.Collections.emptySet()
                        : communes.stream()
                                .filter(dpc -> dpc.getStatutPiece() != null
                                        && "FOURNI".equalsIgnoreCase(dpc.getStatutPiece().getCode()))
                                .map(dpc -> dpc.getCataloguePieceCommune() != null
                                        ? dpc.getCataloguePieceCommune().getId()
                                        : null)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                if (!providedCommuneIds.containsAll(requiredCommuneIds)) {
                    return false;
                }
            }
        }

        // Verify that all catalogue-defined mandatory complementaire pieces are
        // provided with statut FOURNI
        List<CataloguePieceComplementaire> catalogueComplementaires = cataloguePieceComplementaireRepository.findAll();
        if (catalogueComplementaires != null && !catalogueComplementaires.isEmpty()) {
            Set<Long> requiredCompIds = catalogueComplementaires.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getObligatoire()))
                    .map(CataloguePieceComplementaire::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!requiredCompIds.isEmpty()) {
                List<DossierPieceComplementaire> comps = dossierPieceComplementaireRepository
                        .findByDossierId(dossierId);
                Set<Long> providedCompIds = comps == null ? java.util.Collections.emptySet()
                        : comps.stream()
                                .filter(dpc -> dpc.getStatutPiece() != null
                                        && "FOURNI".equalsIgnoreCase(dpc.getStatutPiece().getCode()))
                                .map(dpc -> dpc.getCataloguePieceComplementaire() != null
                                        ? dpc.getCataloguePieceComplementaire().getId()
                                        : null)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                if (!providedCompIds.containsAll(requiredCompIds)) {
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

        if (statutCode == null || statutCode.isBlank())
            throw new ResponseStatusException(BAD_REQUEST, "statutCode requis");

        // If approving, ensure dossier is complete
        if (statutCode.equalsIgnoreCase("APPROUVE") || statutCode.equalsIgnoreCase("APPROVED")
                || statutCode.equalsIgnoreCase("APPROVE")) {
            if (!verifierCompletude(dossierId)) {
                throw new ResponseStatusException(BAD_REQUEST, "dossier incomplet, impossible d'approuver");
            }
        }

        StatutDossier sd = statutDossierRepository.findByCode(statutCode);
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
