package com.s5.framework.dev.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.s5.framework.dev.models.CarteResident;
import com.s5.framework.dev.repositories.CarteResidentRepository;
import com.s5.framework.dev.repositories.CarteResidentRepository.DemandeInfo;

public class CarteResidentService {

    private static final Set<String> ALLOWED_TYPE_CODES = Set.of("travailleur", "investisseur");

    private final CarteResidentRepository carteRepository;

    public CarteResidentService() {
        this.carteRepository = new CarteResidentRepository();
    }

    public List<CarteResident> findAll() {
        return carteRepository.findAll();
    }

    public CarteResident findById(Long id) {
        return carteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carte resident introuvable avec id=" + id));
    }

    public List<CarteResident> findHistoryByPersonne(Long idPersonne) {
        return carteRepository.findByPersonneId(idPersonne);
    }

    public List<CarteResident> search(String numCarte, Long idPersonne, Integer idStatutCarte) {
        return carteRepository.search(normalize(numCarte), idPersonne, idStatutCarte);
    }

    public List<CarteResident> findExpiringWithinDays(Integer jours) {
        int safeDays = (jours == null || jours <= 0) ? 30 : jours;
        return carteRepository.findExpiringWithinDays(safeDays);
    }

    public CarteResident create(CarteResident carte) {
        normalizeAndApplyDefaults(carte);
        validateRequiredFields(carte);
        validateNumCarteForCreate(carte.getNumCarte());
        carte.setIdCarte(null);
        return carteRepository.save(carte);
    }

    public CarteResident update(Long id, CarteResident payload) {
        normalizeAndApplyDefaults(payload);
        validateRequiredFields(payload);

        CarteResident existing = carteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carte resident introuvable avec id=" + id));

        validateNumCarteForUpdate(payload.getNumCarte(), id);

        existing.setIdPersonne(payload.getIdPersonne());
        existing.setIdDemande(payload.getIdDemande());
        existing.setNumCarte(payload.getNumCarte());
        existing.setDateEmission(payload.getDateEmission());
        existing.setDateExpiration(payload.getDateExpiration());
        existing.setIdStatutCarte(payload.getIdStatutCarte());
        existing.setIdTypeCarte(payload.getIdTypeCarte());

        return carteRepository.save(existing);
    }

    public void delete(Long id) {
        if (!carteRepository.existsById(id)) {
            throw new IllegalArgumentException("Carte resident introuvable avec id=" + id);
        }
        carteRepository.deleteById(id);
    }

    public CarteResident genererApresTransformation(CarteResident payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (payload.getIdDemande() == null) {
            throw new IllegalArgumentException("Le champ idDemande est obligatoire pour la generation de carte");
        }

        DemandeInfo info = carteRepository.findDemandeInfo(payload.getIdDemande())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec id=" + payload.getIdDemande()));

        if (!equalsIgnoreCase(info.getTypeDemandeCode(), "transformation")) {
            throw new IllegalArgumentException("La demande doit etre de type transformation");
        }
        if (!equalsIgnoreCase(info.getStatutDemandeCode(), "validee")) {
            throw new IllegalArgumentException("La demande de transformation doit etre validee");
        }

        payload.setIdPersonne(info.getIdPersonne());

        if (payload.getIdTypeCarte() == null && info.getTypeVisaVouluCode() != null) {
            payload.setIdTypeCarte(carteRepository.findTypeCarteIdByCode(info.getTypeVisaVouluCode()));
        }

        if (payload.getIdStatutCarte() == null) {
            payload.setIdStatutCarte(carteRepository.findStatutCarteIdByCode("actif"));
        }

        normalizeAndApplyDefaults(payload);
        validateRequiredFields(payload);
        validateNumCarteForCreate(payload.getNumCarte());
        payload.setIdCarte(null);

        return carteRepository.save(payload);
    }

    public CarteResident changerStatut(Long idCarte, String codeStatut) {
        if (!carteRepository.existsById(idCarte)) {
            throw new IllegalArgumentException("Carte resident introuvable avec id=" + idCarte);
        }
        if (isBlank(codeStatut)) {
            throw new IllegalArgumentException("Le code statut est obligatoire");
        }

        Integer idStatut = carteRepository.findStatutCarteIdByCode(codeStatut.trim());
        if (idStatut == null) {
            throw new IllegalArgumentException("Statut carte inconnu: " + codeStatut);
        }

        return carteRepository.updateStatut(idCarte, idStatut);
    }

    public CarteResident creerDuplicata(Long idCarteOriginal, Long idDemande, CarteResident payload, String motif) {
        if (idCarteOriginal == null) {
            throw new IllegalArgumentException("idCarteOriginal est obligatoire");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }

        CarteResident original = carteRepository.findById(idCarteOriginal)
                .orElseThrow(() -> new IllegalArgumentException("Carte originale introuvable avec id=" + idCarteOriginal));

        String statutOriginalCode = carteRepository.findStatutCarteCodeById(original.getIdStatutCarte());
        if (!equalsIgnoreCase(statutOriginalCode, "perdu")) {
            throw new IllegalArgumentException("Un duplicata exige une carte originale avec statut 'perdu'");
        }

        if (isBlank(payload.getNumCarte())) {
            throw new IllegalArgumentException("Le champ numCarte est obligatoire pour le duplicata");
        }
        validateNumCarteForCreate(payload.getNumCarte());

        Integer idStatutActif = carteRepository.findStatutCarteIdByCode("actif");
        if (idStatutActif == null) {
            throw new IllegalArgumentException("Statut carte 'actif' non configure");
        }

        LocalDate dateDelivrance = payload.getDateEmission() == null ? LocalDate.now() : payload.getDateEmission();
        String motifFinal = normalize(motif);
        if (motifFinal == null) {
            motifFinal = "perte";
        }

        try {
            return carteRepository.createDuplicata(idCarteOriginal, idDemande, payload.getNumCarte().trim(),
                    dateDelivrance, idStatutActif, motifFinal);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public CarteResident transferer(Long idCarteOriginal, CarteResident nouveauPayload, Long idDemande, String motif) {
        if (idCarteOriginal == null) {
            throw new IllegalArgumentException("idCarteOriginal est obligatoire");
        }

        CarteResident original = carteRepository.findById(idCarteOriginal)
                .orElseThrow(() -> new IllegalArgumentException("Carte originale introuvable avec id=" + idCarteOriginal));

        if (nouveauPayload == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }

        nouveauPayload.setIdPersonne(original.getIdPersonne());
        nouveauPayload.setIdDemande(idDemande);

        if (nouveauPayload.getDateEmission() == null) {
            nouveauPayload.setDateEmission(LocalDate.now());
        }
        if (nouveauPayload.getDateExpiration() == null) {
            nouveauPayload.setDateExpiration(nouveauPayload.getDateEmission().plusYears(2));
        }
        if (nouveauPayload.getIdTypeCarte() == null) {
            nouveauPayload.setIdTypeCarte(original.getIdTypeCarte());
        }

        Integer idStatutActif = carteRepository.findStatutCarteIdByCode("actif");
        Integer idStatutTransfert = carteRepository.findStatutCarteIdByCode("transfert");
        if (idStatutActif == null || idStatutTransfert == null) {
            throw new IllegalArgumentException("Statuts carte requis (actif/transfert) non configures");
        }
        nouveauPayload.setIdStatutCarte(idStatutActif);

        normalizeAndApplyDefaults(nouveauPayload);
        validateRequiredFields(nouveauPayload);
        validateNumCarteForCreate(nouveauPayload.getNumCarte());
        nouveauPayload.setIdCarte(null);

        try {
            return carteRepository.transferer(idCarteOriginal, nouveauPayload, idDemande, normalize(motif), idStatutTransfert);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void normalizeAndApplyDefaults(CarteResident carte) {
        if (carte == null) {
            return;
        }

        carte.setNumCarte(normalize(carte.getNumCarte()));

        if (carte.getDateEmission() == null) {
            carte.setDateEmission(LocalDate.now());
        }
        if (carte.getDateExpiration() == null) {
            carte.setDateExpiration(carte.getDateEmission().plusYears(2));
        }
    }

    private void validateRequiredFields(CarteResident carte) {
        if (carte == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (carte.getIdPersonne() == null) {
            throw new IllegalArgumentException("Le champ idPersonne est obligatoire");
        }
        if (isBlank(carte.getNumCarte())) {
            throw new IllegalArgumentException("Le champ numCarte est obligatoire");
        }
        if (carte.getDateEmission() == null) {
            throw new IllegalArgumentException("Le champ dateEmission est obligatoire");
        }
        if (carte.getDateExpiration() == null) {
            throw new IllegalArgumentException("Le champ dateExpiration est obligatoire");
        }
        if (carte.getDateExpiration().isBefore(carte.getDateEmission())) {
            throw new IllegalArgumentException("dateExpiration doit etre superieure ou egale a dateEmission");
        }
        if (carte.getIdTypeCarte() == null) {
            throw new IllegalArgumentException("Le champ idTypeCarte est obligatoire");
        }
        validateAllowedTypeCarte(carte.getIdTypeCarte());

        if (carte.getIdStatutCarte() == null) {
            throw new IllegalArgumentException("Le champ idStatutCarte est obligatoire");
        }
    }

    private void validateAllowedTypeCarte(Integer idTypeCarte) {
        String code = carteRepository.findTypeCarteCodeById(idTypeCarte);
        if (code == null) {
            throw new IllegalArgumentException("Type de carte introuvable: id=" + idTypeCarte);
        }

        if (!ALLOWED_TYPE_CODES.contains(code.trim().toLowerCase())) {
            throw new IllegalArgumentException("Seuls les types travailleur et investisseur sont autorises");
        }
    }

    private void validateNumCarteForCreate(String numCarte) {
        if (!isBlank(numCarte) && carteRepository.existsByNumCarte(numCarte.trim())) {
            throw new IllegalArgumentException("Ce numero de carte est deja utilise");
        }
    }

    private void validateNumCarteForUpdate(String numCarte, Long idCarte) {
        if (!isBlank(numCarte) && carteRepository.existsByNumCarteAndIdCarteNot(numCarte.trim(), idCarte)) {
            throw new IllegalArgumentException("Ce numero de carte est deja utilise");
        }
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
