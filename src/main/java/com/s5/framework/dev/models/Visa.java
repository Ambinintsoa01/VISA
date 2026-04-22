package com.s5.framework.dev.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.maharavo.flame.persistence.Column;
import com.maharavo.flame.persistence.Entity;
import com.maharavo.flame.persistence.Table;

@Entity
@Table(name = "visa")
public class Visa {

    @Column(name = "id_visa")
    private Long idVisa;

    @Column(name = "id_personne", nullable = false)
    private Long idPersonne;

    @Column(name = "num_visa", nullable = false, unique = true, length = 50)
    private String numVisa;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "id_statut_visa")
    private Integer idStatutVisa;

    @Column(name = "est_transformable")
    private Boolean estTransformable;

    @Column(name = "est_connu_interieur")
    private Boolean estConnuInterieur;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getIdVisa() {
        return idVisa;
    }

    public void setIdVisa(Long idVisa) {
        this.idVisa = idVisa;
    }

    public Long getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Long idPersonne) {
        this.idPersonne = idPersonne;
    }

    public String getNumVisa() {
        return numVisa;
    }

    public void setNumVisa(String numVisa) {
        this.numVisa = numVisa;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getIdStatutVisa() {
        return idStatutVisa;
    }

    public void setIdStatutVisa(Integer idStatutVisa) {
        this.idStatutVisa = idStatutVisa;
    }

    public Boolean getEstTransformable() {
        return estTransformable;
    }

    public void setEstTransformable(Boolean estTransformable) {
        this.estTransformable = estTransformable;
    }

    public Boolean getEstConnuInterieur() {
        return estConnuInterieur;
    }

    public void setEstConnuInterieur(Boolean estConnuInterieur) {
        this.estConnuInterieur = estConnuInterieur;
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
