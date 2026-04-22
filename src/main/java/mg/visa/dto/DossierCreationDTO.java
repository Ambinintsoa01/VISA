package mg.visa.dto;

public class DossierCreationDTO {
    private Long demandeId;
    private String createdBy;

    public DossierCreationDTO() {}

    public Long getDemandeId() { return demandeId; }
    public void setDemandeId(Long demandeId) { this.demandeId = demandeId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
