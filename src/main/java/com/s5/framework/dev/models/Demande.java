package com.s5.framework.dev.models;

import java.time.LocalDateTime;

import com.maharavo.flame.persistence.Column;
import com.maharavo.flame.persistence.Entity;
import com.maharavo.flame.persistence.Table;

@Entity
@Table(name = "demande")
public class Demande {

    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "id_personne", nullable = false)
    private Long idPersonne;

    @Column(name = "id_visa_original")
    private Long idVisaOriginal;

    @Column(name = "id_type_demande")
    private Integer idTypeDemande;

    @Column(name = "id_type_visa_voulu")
    private Integer idTypeVisaVoulu;

    @Column(name = "id_statut_demande")
    private Integer idStatutDemande;

    @Column(name = "sans_donne_interieur")
    private Boolean sansDonneInterieur;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    @Column(name = "motif")
    private String motif;

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public Long getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Long idPersonne) {
        this.idPersonne = idPersonne;
    }

    public Long getIdVisaOriginal() {
        return idVisaOriginal;
    }

    public void setIdVisaOriginal(Long idVisaOriginal) {
        this.idVisaOriginal = idVisaOriginal;
    }

    public Integer getIdTypeDemande() {
        return idTypeDemande;
    }

    public void setIdTypeDemande(Integer idTypeDemande) {
        this.idTypeDemande = idTypeDemande;
    }

    public Integer getIdTypeVisaVoulu() {
        return idTypeVisaVoulu;
    }

    public void setIdTypeVisaVoulu(Integer idTypeVisaVoulu) {
        this.idTypeVisaVoulu = idTypeVisaVoulu;
    }

    public Integer getIdStatutDemande() {
        return idStatutDemande;
    }

    public void setIdStatutDemande(Integer idStatutDemande) {
        this.idStatutDemande = idStatutDemande;
    }

    public Boolean getSansDonneInterieur() {
        return sansDonneInterieur;
    }

    public void setSansDonneInterieur(Boolean sansDonneInterieur) {
        this.sansDonneInterieur = sansDonneInterieur;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getTraitePar() {
        return traitePar;
    }

    public void setTraitePar(String traitePar) {
        this.traitePar = traitePar;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
}
