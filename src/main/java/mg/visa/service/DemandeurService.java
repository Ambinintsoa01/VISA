package mg.visa.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.dto.DemandeurDTO;
import mg.visa.entity.Demandeur;
import mg.visa.entity.Sexe;
import mg.visa.entity.ref.Nationalite;
import mg.visa.entity.ref.SituationFamiliale;
import mg.visa.repository.DemandeurRepository;
import mg.visa.repository.SexeRepository;
import mg.visa.repository.ref.NationaliteRepository;
import mg.visa.repository.ref.SituationFamilialeRepository;

@Service
public class DemandeurService {

    private final DemandeurRepository demandeurRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final SexeRepository sexeRepository;

    public DemandeurService(DemandeurRepository demandeurRepository,
                           NationaliteRepository nationaliteRepository,
                           SituationFamilialeRepository situationFamilialeRepository,
                           SexeRepository sexeRepository) {
        this.demandeurRepository = demandeurRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.sexeRepository = sexeRepository;
    }

    public Demandeur createDemandeur(DemandeurDTO dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "nom est requis");
        }

        Demandeur d = new Demandeur();
        d.setNom(dto.getNom());
        d.setPrenom(dto.getPrenom());
        d.setDateNaissance(dto.getDateNaissance());
        d.setEmail(dto.getEmail());
        d.setTelephone(dto.getTelephone());

        if (dto.getNationaliteId() != null) {
            Nationalite n = nationaliteRepository.findById(dto.getNationaliteId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "nationalite introuvable"));
            d.setNationalite(n);
        }

        if (dto.getSituationFamilialeId() != null) {
            SituationFamiliale s = situationFamilialeRepository.findById(dto.getSituationFamilialeId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "situation familiale introuvable"));
            d.setSituationFamiliale(s);
        }

        if (dto.getSexeId() != null) {
            Sexe s = sexeRepository.findById(dto.getSexeId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "sexe introuvable"));
            d.setSexe(s);
        }

        return demandeurRepository.save(d);
    }
}
