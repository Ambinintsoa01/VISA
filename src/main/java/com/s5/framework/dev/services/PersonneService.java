package com.s5.framework.dev.services;

import java.util.List;

import com.s5.framework.dev.models.Personne;
import com.s5.framework.dev.repositories.PersonneRepository;

public class PersonneService {

    private final PersonneRepository personneRepository;

    public PersonneService() {
        this.personneRepository = new PersonneRepository();
    }

    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    public Personne findById(Long id) {
        return personneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personne introuvable avec id=" + id));
    }

    public Personne create(Personne personne) {
        validateRequiredFields(personne);
        validateEmailForCreate(personne.getEmail());
        personne.setIdPersonne(null);
        return personneRepository.save(personne);
    }

    public Personne update(Long id, Personne payload) {
        validateRequiredFields(payload);

        Personne existing = personneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personne introuvable avec id=" + id));

        validateEmailForUpdate(payload.getEmail(), id);

        existing.setNom(payload.getNom());
        existing.setNomJeuneFille(payload.getNomJeuneFille());
        existing.setDateNaissance(payload.getDateNaissance());
        existing.setLieuNaissance(payload.getLieuNaissance());
        existing.setIdSituationFamiliale(payload.getIdSituationFamiliale());
        existing.setIdNationalite(payload.getIdNationalite());
        existing.setIdProfession(payload.getIdProfession());
        existing.setAdresse(payload.getAdresse());
        existing.setContact(payload.getContact());
        existing.setEmail(payload.getEmail());
        existing.setPhotoUrl(payload.getPhotoUrl());

        return personneRepository.save(existing);
    }

    public void delete(Long id) {
        if (!personneRepository.existsById(id)) {
            throw new IllegalArgumentException("Personne introuvable avec id=" + id);
        }
        personneRepository.deleteById(id);
    }

    public List<Personne> search(String nom, String email, String numVisa, String numCarte) {
        return personneRepository.search(normalize(nom), normalize(email), normalize(numVisa), normalize(numCarte));
    }

    private void validateRequiredFields(Personne personne) {
        if (personne == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (isBlank(personne.getNom())) {
            throw new IllegalArgumentException("Le champ nom est obligatoire");
        }
        if (personne.getDateNaissance() == null) {
            throw new IllegalArgumentException("Le champ dateNaissance est obligatoire");
        }
    }

    private void validateEmailForCreate(String email) {
        if (!isBlank(email) && personneRepository.existsByEmail(email.trim())) {
            throw new IllegalArgumentException("Cet email est deja utilise");
        }
    }

    private void validateEmailForUpdate(String email, Long id) {
        if (!isBlank(email) && personneRepository.existsByEmailAndIdPersonneNot(email.trim(), id)) {
            throw new IllegalArgumentException("Cet email est deja utilise");
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
