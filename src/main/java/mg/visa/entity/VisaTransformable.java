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
import mg.visa.entity.ref.TypeVisa;

@Entity
@Table(name = "visa_transformable")
public class VisaTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "passeport_id")
    private Passeport passeport;

    @ManyToOne
    @JoinColumn(name = "type_visa_id")
    private TypeVisa typeVisa;

    @Column(name = "infos", columnDefinition = "text")
    private String infos; // JSON text

    @Column(name = "remarque")
    private String remarque;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public VisaTransformable() {}

    @PrePersist
    protected void onCreate() { createdAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) { this.passeport = passeport; }

    public TypeVisa getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeVisa typeVisa) { this.typeVisa = typeVisa; }

    public String getInfos() { return infos; }
    public void setInfos(String infos) { this.infos = infos; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
