package com.s5.framework.dev.services;

import java.util.List;
import java.util.Set;

import com.s5.framework.dev.models.Demande;
import com.s5.framework.dev.repositories.DemandeRepository;

public class DemandeService {

    private static final Set<String> ALLOWED_TYPE_CODES = Set.of("travailleur", "investisseur");
    private static final String SANS_DONNE_INTERIEUR_MARKER = "SANS_DONNE_INTERIEUR";

    private final DemandeRepository demandeRepository;

    public DemandeService() {
        this.demandeRepository = new DemandeRepository();
    }

    public List<Demande> findAll() {
        return demandeRepository.findAll();
    }

    public Demande findById(Long id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec id=" + id));
    }

    public List<Demande> findHistoryByPersonne(Long idPersonne) {
        return demandeRepository.findByPersonneId(idPersonne);
    }

    public List<Demande> findEnAttente() {
        return demandeRepository.findEnAttente();
    }

    public List<Demande> search(Long idPersonne, Integer idStatutDemande, Integer idTypeDemande, Boolean sansDonneInterieur) {
        return demandeRepository.search(idPersonne, idStatutDemande, idTypeDemande, sansDonneInterieur);
    }

    public Demande create(Demande demande) {
        validateRequiredFields(demande);
        applySansDonneInterieurMarker(demande);
        if (demande.getIdStatutDemande() == null) {
            Integer idEnAttente = demandeRepository.findStatutDemandeIdByCode("en_attente");
            demande.setIdStatutDemande(idEnAttente);
        }
        demande.setIdDemande(null);
        return demandeRepository.save(demande);
    }

    public Demande createTransformation(Demande demande) {
        return createByTypeCode("transformation", demande);
    }

    public Demande createDuplicata(Demande demande) {
        return createByTypeCode("duplicata", demande);
    }

    public Demande createTransfertVisa(Demande demande) {
        return createByTypeCode("transfert_visa", demande);
    }

    public Demande createTransfertCarte(Demande demande) {
        return createByTypeCode("transfert_carte", demande);
    }

    public Demande update(Long id, Demande payload) {
        validateRequiredFields(payload);
        applySansDonneInterieurMarker(payload);

        Demande existing = demandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec id=" + id));

        existing.setIdPersonne(payload.getIdPersonne());
        existing.setIdVisaOriginal(payload.getIdVisaOriginal());
        existing.setIdTypeDemande(payload.getIdTypeDemande());
        existing.setIdTypeVisaVoulu(payload.getIdTypeVisaVoulu());
        existing.setIdStatutDemande(payload.getIdStatutDemande());
        existing.setSansDonneInterieur(defaultBoolean(payload.getSansDonneInterieur()));
        existing.setMotif(payload.getMotif());
        existing.setTraitePar(payload.getTraitePar());
        existing.setDateTraitement(payload.getDateTraitement());

        return demandeRepository.save(existing);
    }

    public void delete(Long id) {
        if (!demandeRepository.existsById(id)) {
            throw new IllegalArgumentException("Demande introuvable avec id=" + id);
        }
        demandeRepository.deleteById(id);
    }

    public Demande changerStatut(Long idDemande, String codeStatut, String traitePar) {
        if (!demandeRepository.existsById(idDemande)) {
            throw new IllegalArgumentException("Demande introuvable avec id=" + idDemande);
        }
        if (isBlank(codeStatut)) {
            throw new IllegalArgumentException("Le code statut est obligatoire");
        }

        Integer idStatut = demandeRepository.findStatutDemandeIdByCode(codeStatut.trim());
        if (idStatut == null) {
            throw new IllegalArgumentException("Statut demande inconnu: " + codeStatut);
        }

        return demandeRepository.updateStatut(idDemande, idStatut, normalize(traitePar));
    }

    private Demande createByTypeCode(String typeCode, Demande demande) {
        if (demande == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }

        Integer idTypeDemande = demandeRepository.findTypeDemandeIdByCode(typeCode);
        if (idTypeDemande == null) {
            throw new IllegalArgumentException("Type de demande inconnu: " + typeCode);
        }

        if ("transformation".equalsIgnoreCase(typeCode)) {
            enforceTransformationRules(demande);
        }

        demande.setIdTypeDemande(idTypeDemande);
        return create(demande);
    }

    private void validateRequiredFields(Demande demande) {
        if (demande == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (demande.getIdPersonne() == null) {
            throw new IllegalArgumentException("Le champ idPersonne est obligatoire");
        }
        if (demande.getIdTypeDemande() == null) {
            throw new IllegalArgumentException("Le champ idTypeDemande est obligatoire");
        }

        validateAllowedTypeVisaVoulu(demande.getIdTypeVisaVoulu());
    }

    private void enforceTransformationRules(Demande demande) {
        if (demande.getIdVisaOriginal() == null) {
            throw new IllegalArgumentException("Une demande de transformation exige idVisaOriginal");
        }

        Boolean transformable = demandeRepository.isVisaTransformable(demande.getIdVisaOriginal());
        if (transformable == null) {
            throw new IllegalArgumentException("Visa original introuvable avec id=" + demande.getIdVisaOriginal());
        }
        if (!transformable) {
            throw new IllegalArgumentException("Un VISA non transformable ne peut pas etre transforme");
        }

        if (demandeRepository.hasTransformationDemandeForVisa(demande.getIdVisaOriginal())) {
            throw new IllegalArgumentException("Un VISA transformable ne peut etre transforme qu'une seule fois");
        }
    }

    private void validateAllowedTypeVisaVoulu(Integer idTypeVisaVoulu) {
        if (idTypeVisaVoulu == null) {
            return;
        }

        String code = demandeRepository.findTypeVisaVouluCodeById(idTypeVisaVoulu);
        if (code == null) {
            throw new IllegalArgumentException("Type de visa voulu introuvable: id=" + idTypeVisaVoulu);
        }

        if (!ALLOWED_TYPE_CODES.contains(code.trim().toLowerCase())) {
            throw new IllegalArgumentException("Seuls les types travailleur et investisseur sont autorises");
        }
    }

    private void applySansDonneInterieurMarker(Demande demande) {
        boolean sansDonneInterieur = defaultBoolean(demande.getSansDonneInterieur());
        demande.setSansDonneInterieur(sansDonneInterieur);

        if (!sansDonneInterieur) {
            return;
        }

        String motif = normalize(demande.getMotif());
        if (motif == null) {
            demande.setMotif(SANS_DONNE_INTERIEUR_MARKER);
            return;
        }

        String upperMotif = motif.toUpperCase();
        if (!upperMotif.contains(SANS_DONNE_INTERIEUR_MARKER)) {
            demande.setMotif(SANS_DONNE_INTERIEUR_MARKER + " - " + motif);
            return;
        }

        demande.setMotif(motif);
    }

    private Boolean defaultBoolean(Boolean value) {
        return value != null && value;
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
