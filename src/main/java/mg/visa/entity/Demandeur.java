package mg.visa.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import mg.visa.entity.ref.Nationalite;
import mg.visa.entity.ref.SituationFamiliale;

@Entity
@Table(name = "demandeur")
public class Demandeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "date_naissance")
    private java.time.LocalDate dateNaissance;

    @ManyToOne
    @JoinColumn(name = "sexe_id")
    private Sexe sexe;

    @ManyToOne
    @JoinColumn(name = "nationalite_id")
    private Nationalite nationalite;

    @ManyToOne
    @JoinColumn(name = "situation_familiale_id")
    private SituationFamiliale situationFamiliale;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Demandeur() {}

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public java.time.LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(java.time.LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }

    public Nationalite getNationalite() { return nationalite; }
    public void setNationalite(Nationalite nationalite) { this.nationalite = nationalite; }

    public SituationFamiliale getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(SituationFamiliale situationFamiliale) { this.situationFamiliale = situationFamiliale; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
