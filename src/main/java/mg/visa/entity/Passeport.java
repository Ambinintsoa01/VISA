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
import jakarta.persistence.Table;

@Entity
@Table(name = "passeport")
public class Passeport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "pays_emission")
    private String paysEmission;

    @Column(name = "date_emission")
    private java.time.LocalDate dateEmission;

    @Column(name = "date_expiration")
    private java.time.LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Demandeur demandeur;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Passeport() {}

    @PrePersist
    protected void onCreate() { createdAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getPaysEmission() { return paysEmission; }
    public void setPaysEmission(String paysEmission) { this.paysEmission = paysEmission; }

    public java.time.LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(java.time.LocalDate dateEmission) { this.dateEmission = dateEmission; }

    public java.time.LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(java.time.LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
