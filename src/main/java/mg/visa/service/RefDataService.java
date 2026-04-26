package mg.visa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.visa.entity.ref.Nationalite;
import mg.visa.entity.ref.SituationFamiliale;
import mg.visa.entity.ref.TypeDemande;
import mg.visa.entity.ref.TypeIdentite;
import mg.visa.entity.ref.TypeVisa;
import mg.visa.repository.ref.NationaliteRepository;
import mg.visa.repository.ref.SituationFamilialeRepository;
import mg.visa.repository.ref.TypeDemandeRepository;
import mg.visa.repository.ref.TypeIdentiteRepository;
import mg.visa.repository.ref.TypeVisaRepository;

@Service
public class RefDataService {

    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final TypeIdentiteRepository typeIdentiteRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;

    public RefDataService(NationaliteRepository nationaliteRepository,
                          SituationFamilialeRepository situationFamilialeRepository,
                          TypeIdentiteRepository typeIdentiteRepository,
                          TypeVisaRepository typeVisaRepository,
                          TypeDemandeRepository typeDemandeRepository) {
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.typeIdentiteRepository = typeIdentiteRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
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

    public List<TypeVisa> getTypesVisas() {
        return typeVisaRepository.findAll();
    }

    public List<TypeDemande> getTypesDemandes() {
        return typeDemandeRepository.findAll();
    }
}
