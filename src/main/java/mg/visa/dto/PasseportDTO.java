package mg.visa.dto;

import java.time.LocalDate;

public class PasseportDTO {
    private Long id;
    private String numero;
    private String paysEmission;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private Long demandeurId;

    public PasseportDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getPaysEmission() { return paysEmission; }
    public void setPaysEmission(String paysEmission) { this.paysEmission = paysEmission; }

    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Long getDemandeurId() { return demandeurId; }
    public void setDemandeurId(Long demandeurId) { this.demandeurId = demandeurId; }
}
