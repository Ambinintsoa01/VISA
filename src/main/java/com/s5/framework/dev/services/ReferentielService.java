package com.s5.framework.dev.services;

import java.util.List;
import java.util.Map;

import com.s5.framework.dev.models.ReferentielItem;
import com.s5.framework.dev.repositories.ReferentielRepository;

public class ReferentielService {

    private final ReferentielRepository repository;

    public ReferentielService() {
        this.repository = new ReferentielRepository();
    }

    public Map<String, List<ReferentielItem>> getAllReferentielsActifs() {
        return repository.listAllActive();
    }

    public List<ReferentielItem> getByType(String type, Boolean actifOnly) {
        validateType(type);
        boolean active = actifOnly == null || actifOnly;
        return repository.listByType(type, active);
    }

    public ReferentielItem create(String type, ReferentielItem payload) {
        validateType(type);
        validatePayload(payload);

        payload.setCode(normalize(payload.getCode()));
        payload.setLibelle(normalize(payload.getLibelle()));
        payload.setDescription(normalize(payload.getDescription()));

        if (payload.getActif() == null) {
            payload.setActif(true);
        }

        if (repository.existsByCode(type, payload.getCode())) {
            throw new IllegalArgumentException("Ce code existe deja dans ce referentiel");
        }

        return repository.create(type, payload);
    }

    private void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type referentiel est obligatoire");
        }
        if (!repository.supportsType(type)) {
            throw new IllegalArgumentException("Type referentiel non supporte: " + type);
        }
    }

    private void validatePayload(ReferentielItem payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (isBlank(payload.getCode())) {
            throw new IllegalArgumentException("Le champ code est obligatoire");
        }
        if (isBlank(payload.getLibelle())) {
            throw new IllegalArgumentException("Le champ libelle est obligatoire");
        }
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
