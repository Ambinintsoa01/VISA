package com.s5.framework.dev.services;

import java.util.List;

import com.s5.framework.dev.models.CarteResident;
import com.s5.framework.dev.models.Demande;
import com.s5.framework.dev.models.MultiCritereSearchResult;
import com.s5.framework.dev.models.Personne;
import com.s5.framework.dev.models.Visa;

public class MultiCritereSearchService {

    private final PersonneService personneService;
    private final VisaService visaService;
    private final DemandeService demandeService;
    private final CarteResidentService carteResidentService;

    public MultiCritereSearchService() {
        this.personneService = new PersonneService();
        this.visaService = new VisaService();
        this.demandeService = new DemandeService();
        this.carteResidentService = new CarteResidentService();
    }

    public MultiCritereSearchResult search(
            String nom,
            String email,
            String numVisa,
            String numCarte,
            Long idPersonne,
            Integer idStatutVisa,
            Integer idStatutDemande,
            Integer idTypeDemande,
            Integer idStatutCarte,
            Boolean sansDonneInterieur
    ) {
        List<Personne> personnes = personneService.search(nom, email, numVisa, numCarte);
        List<Visa> visas = visaService.search(numVisa, idPersonne, idStatutVisa);
        List<Demande> demandes = demandeService.search(idPersonne, idStatutDemande, idTypeDemande, sansDonneInterieur);
        List<CarteResident> cartes = carteResidentService.search(numCarte, idPersonne, idStatutCarte);

        MultiCritereSearchResult result = new MultiCritereSearchResult();
        result.setPersonnes(personnes);
        result.setVisas(visas);
        result.setDemandes(demandes);
        result.setCartesResident(cartes);

        result.setTotalPersonnes(personnes.size());
        result.setTotalVisas(visas.size());
        result.setTotalDemandes(demandes.size());
        result.setTotalCartesResident(cartes.size());

        return result;
    }
}
