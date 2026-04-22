package com.s5.framework.dev.services;

import java.util.List;

import com.s5.framework.dev.models.Visa;
import com.s5.framework.dev.repositories.VisaRepository;

public class VisaService {

    private final VisaRepository visaRepository;

    public VisaService() {
        this.visaRepository = new VisaRepository();
    }

    public List<Visa> findAll() {
        return visaRepository.findAll();
    }

    public Visa findById(Long id) {
        return visaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visa introuvable avec id=" + id));
    }

    public List<Visa> findHistoryByPersonne(Long idPersonne) {
        return visaRepository.findByPersonneId(idPersonne);
    }

    public List<Visa> search(String numVisa, Long idPersonne, Integer idStatutVisa) {
        return visaRepository.search(normalize(numVisa), idPersonne, idStatutVisa);
    }

    public Visa create(Visa visa) {
        validateRequiredFields(visa);
        validateNumVisaForCreate(visa.getNumVisa());
        visa.setIdVisa(null);
        return visaRepository.save(visa);
    }

    public Visa update(Long id, Visa payload) {
        validateRequiredFields(payload);

        Visa existing = visaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visa introuvable avec id=" + id));

        validateNumVisaForUpdate(payload.getNumVisa(), id);

        existing.setIdPersonne(payload.getIdPersonne());
        existing.setNumVisa(payload.getNumVisa());
        existing.setDateEntree(payload.getDateEntree());
        existing.setDateFin(payload.getDateFin());
        existing.setIdStatutVisa(payload.getIdStatutVisa());
        existing.setEstTransformable(defaultBoolean(payload.getEstTransformable()));
        existing.setEstConnuInterieur(defaultBoolean(payload.getEstConnuInterieur()));

        return visaRepository.save(existing);
    }

    public void delete(Long id) {
        if (!visaRepository.existsById(id)) {
            throw new IllegalArgumentException("Visa introuvable avec id=" + id);
        }
        visaRepository.deleteById(id);
    }

    public Visa marquerTransformable(Long id, Boolean value) {
        requireVisaExists(id);
        return visaRepository.updateTransformable(id, defaultBoolean(value));
    }

    public Visa marquerConnuInterieur(Long id, Boolean value) {
        requireVisaExists(id);
        return visaRepository.updateConnuInterieur(id, defaultBoolean(value));
    }

    public Visa transferer(Long idVisaOriginal, Visa nouveauVisa, String motif) {
        Visa original = visaRepository.findById(idVisaOriginal)
                .orElseThrow(() -> new IllegalArgumentException("Visa original introuvable avec id=" + idVisaOriginal));

        if (nouveauVisa == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }

        if (nouveauVisa.getIdPersonne() == null) {
            nouveauVisa.setIdPersonne(original.getIdPersonne());
        }
        if (nouveauVisa.getDateEntree() == null) {
            nouveauVisa.setDateEntree(original.getDateEntree());
        }
        if (nouveauVisa.getDateFin() == null) {
            nouveauVisa.setDateFin(original.getDateFin());
        }
        if (nouveauVisa.getIdStatutVisa() == null) {
            nouveauVisa.setIdStatutVisa(original.getIdStatutVisa());
        }
        if (nouveauVisa.getEstTransformable() == null) {
            nouveauVisa.setEstTransformable(original.getEstTransformable());
        }
        if (nouveauVisa.getEstConnuInterieur() == null) {
            nouveauVisa.setEstConnuInterieur(original.getEstConnuInterieur());
        }

        validateRequiredFields(nouveauVisa);
        validateNumVisaForCreate(nouveauVisa.getNumVisa());
        nouveauVisa.setIdVisa(null);

        return visaRepository.transferer(idVisaOriginal, nouveauVisa, normalize(motif));
    }

    private void validateRequiredFields(Visa visa) {
        if (visa == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (visa.getIdPersonne() == null) {
            throw new IllegalArgumentException("Le champ idPersonne est obligatoire");
        }
        if (isBlank(visa.getNumVisa())) {
            throw new IllegalArgumentException("Le champ numVisa est obligatoire");
        }
        if (visa.getDateEntree() == null) {
            throw new IllegalArgumentException("Le champ dateEntree est obligatoire");
        }
        if (visa.getDateFin() == null) {
            throw new IllegalArgumentException("Le champ dateFin est obligatoire");
        }
        if (visa.getDateFin().isBefore(visa.getDateEntree())) {
            throw new IllegalArgumentException("dateFin doit etre superieure ou egale a dateEntree");
        }
    }

    private void validateNumVisaForCreate(String numVisa) {
        if (!isBlank(numVisa) && visaRepository.existsByNumVisa(numVisa.trim())) {
            throw new IllegalArgumentException("Ce numero de visa est deja utilise");
        }
    }

    private void validateNumVisaForUpdate(String numVisa, Long id) {
        if (!isBlank(numVisa) && visaRepository.existsByNumVisaAndIdVisaNot(numVisa.trim(), id)) {
            throw new IllegalArgumentException("Ce numero de visa est deja utilise");
        }
    }

    private void requireVisaExists(Long id) {
        if (!visaRepository.existsById(id)) {
            throw new IllegalArgumentException("Visa introuvable avec id=" + id);
        }
    }

    private boolean defaultBoolean(Boolean value) {
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
