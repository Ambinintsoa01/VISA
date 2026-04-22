package com.s5.framework.dev.models;

import java.util.List;

public class MultiCritereSearchResult {

    private List<Personne> personnes;
    private List<Visa> visas;
    private List<Demande> demandes;
    private List<CarteResident> cartesResident;

    private Integer totalPersonnes;
    private Integer totalVisas;
    private Integer totalDemandes;
    private Integer totalCartesResident;

    public List<Personne> getPersonnes() {
        return personnes;
    }

    public void setPersonnes(List<Personne> personnes) {
        this.personnes = personnes;
    }

    public List<Visa> getVisas() {
        return visas;
    }

    public void setVisas(List<Visa> visas) {
        this.visas = visas;
    }

    public List<Demande> getDemandes() {
        return demandes;
    }

    public void setDemandes(List<Demande> demandes) {
        this.demandes = demandes;
    }

    public List<CarteResident> getCartesResident() {
        return cartesResident;
    }

    public void setCartesResident(List<CarteResident> cartesResident) {
        this.cartesResident = cartesResident;
    }

    public Integer getTotalPersonnes() {
        return totalPersonnes;
    }

    public void setTotalPersonnes(Integer totalPersonnes) {
        this.totalPersonnes = totalPersonnes;
    }

    public Integer getTotalVisas() {
        return totalVisas;
    }

    public void setTotalVisas(Integer totalVisas) {
        this.totalVisas = totalVisas;
    }

    public Integer getTotalDemandes() {
        return totalDemandes;
    }

    public void setTotalDemandes(Integer totalDemandes) {
        this.totalDemandes = totalDemandes;
    }

    public Integer getTotalCartesResident() {
        return totalCartesResident;
    }

    public void setTotalCartesResident(Integer totalCartesResident) {
        this.totalCartesResident = totalCartesResident;
    }
}
