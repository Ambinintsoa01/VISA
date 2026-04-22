package com.s5.framework.dev.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.maharavo.flame.persistence.Column;
import com.maharavo.flame.persistence.Entity;
import com.maharavo.flame.persistence.Table;

@Entity
@Table(name = "carte_resident")
public class CarteResident {

    @Column(name = "id_carte")
    private Long idCarte;

    @Column(name = "id_personne", nullable = false)
    private Long idPersonne;

    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "num_carte", nullable = false, unique = true, length = 50)
    private String numCarte;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @Column(name = "id_statut_carte")
    private Integer idStatutCarte;

    @Column(name = "id_type_carte")
    private Integer idTypeCarte;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getIdCarte() {
        return idCarte;
    }

    public void setIdCarte(Long idCarte) {
        this.idCarte = idCarte;
    }

    public Long getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Long idPersonne) {
        this.idPersonne = idPersonne;
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public String getNumCarte() {
        return numCarte;
    }

    public void setNumCarte(String numCarte) {
        this.numCarte = numCarte;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Integer getIdStatutCarte() {
        return idStatutCarte;
    }

    public void setIdStatutCarte(Integer idStatutCarte) {
        this.idStatutCarte = idStatutCarte;
    }

    public Integer getIdTypeCarte() {
        return idTypeCarte;
    }

    public void setIdTypeCarte(Integer idTypeCarte) {
        this.idTypeCarte = idTypeCarte;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
