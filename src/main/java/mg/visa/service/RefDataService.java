package mg.visa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.visa.entity.ref.Nationalite;
import mg.visa.entity.ref.SituationFamiliale;
import mg.visa.entity.ref.TypeIdentite;
import mg.visa.repository.ref.NationaliteRepository;
import mg.visa.repository.ref.SituationFamilialeRepository;
import mg.visa.repository.ref.TypeIdentiteRepository;

@Service
public class RefDataService {

    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final TypeIdentiteRepository typeIdentiteRepository;

    public RefDataService(NationaliteRepository nationaliteRepository,
                          SituationFamilialeRepository situationFamilialeRepository,
                          TypeIdentiteRepository typeIdentiteRepository) {
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.typeIdentiteRepository = typeIdentiteRepository;
    }

    public List<Nationalite> getNationalites() {
        return nationaliteRepository.findAll();
    }

    public List<SituationFamiliale> getSituationsFamiliales() {
        return situationFamilialeRepository.findAll();
    }

    public List<TypeIdentite> getTypesIdentite() {
        return typeIdentiteRepository.findAll();
    }
}
