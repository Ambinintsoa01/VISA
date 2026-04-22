package mg.visa.entity.ref;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nationalite")
public class Nationalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_iso")
    private String codeIso;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    public Nationalite() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeIso() { return codeIso; }
    public void setCodeIso(String codeIso) { this.codeIso = codeIso; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
