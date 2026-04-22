package mg.visa.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.visa.entity.ref.StatutPiece;

@Entity
@Table(name = "dossier_piece_complementaire")
public class DossierPieceComplementaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dossier_id", nullable = false)
    private Dossier dossier;

    @ManyToOne
    @JoinColumn(name = "catalogue_piece_complementaire_id", nullable = false)
    private CataloguePieceComplementaire cataloguePieceComplementaire;

    @ManyToOne
    @JoinColumn(name = "statut_piece_id")
    private StatutPiece statutPiece;

    @Column(name = "fichier_path")
    private String fichierPath;

    @Column(name = "date_fourni")
    private OffsetDateTime dateFourni;

    @Column(name = "remarque")
    private String remarque;

    public DossierPieceComplementaire() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Dossier getDossier() { return dossier; }
    public void setDossier(Dossier dossier) { this.dossier = dossier; }

    public CataloguePieceComplementaire getCataloguePieceComplementaire() { return cataloguePieceComplementaire; }
    public void setCataloguePieceComplementaire(CataloguePieceComplementaire cataloguePieceComplementaire) { this.cataloguePieceComplementaire = cataloguePieceComplementaire; }

    public StatutPiece getStatutPiece() { return statutPiece; }
    public void setStatutPiece(StatutPiece statutPiece) { this.statutPiece = statutPiece; }

    public String getFichierPath() { return fichierPath; }
    public void setFichierPath(String fichierPath) { this.fichierPath = fichierPath; }

    public OffsetDateTime getDateFourni() { return dateFourni; }
    public void setDateFourni(OffsetDateTime dateFourni) { this.dateFourni = dateFourni; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
