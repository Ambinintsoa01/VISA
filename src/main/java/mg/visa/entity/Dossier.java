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
import mg.visa.entity.ref.StatutDossier;

@Entity
@Table(name = "dossier")
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    @Column(name = "numero_dossier", unique = true)
    private String numeroDossier;

    @ManyToOne
    @JoinColumn(name = "statut_dossier_id")
    private StatutDossier statutDossier;

    @Column(name = "date_creation")
    private OffsetDateTime dateCreation;

    @Column(name = "date_cloture")
    private OffsetDateTime dateCloture;

    @Column(name = "created_by")
    private String createdBy;

    public Dossier() {}

    @PrePersist
    protected void onCreate() { dateCreation = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }

    public String getNumeroDossier() { return numeroDossier; }
    public void setNumeroDossier(String numeroDossier) { this.numeroDossier = numeroDossier; }

    public StatutDossier getStatutDossier() { return statutDossier; }
    public void setStatutDossier(StatutDossier statutDossier) { this.statutDossier = statutDossier; }

    public OffsetDateTime getDateCreation() { return dateCreation; }
    public OffsetDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(OffsetDateTime dateCloture) { this.dateCloture = dateCloture; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
