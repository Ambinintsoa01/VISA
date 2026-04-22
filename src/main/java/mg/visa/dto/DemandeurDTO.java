package mg.visa.dto;

import java.time.LocalDate;

public class DemandeurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Long sexeId;
    private Long nationaliteId;
    private Long situationFamilialeId;
    private String email;
    private String telephone;

    public DemandeurDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Long getSexeId() { return sexeId; }
    public void setSexeId(Long sexeId) { this.sexeId = sexeId; }

    public Long getNationaliteId() { return nationaliteId; }
    public void setNationaliteId(Long nationaliteId) { this.nationaliteId = nationaliteId; }

    public Long getSituationFamilialeId() { return situationFamilialeId; }
    public void setSituationFamilialeId(Long situationFamilialeId) { this.situationFamilialeId = situationFamilialeId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
