package mg.visa.dto;

import java.util.List;

public class DossierCreationDTO {
    private Long demandeId;
    private String createdBy;
    private List<Long> selectedCommuneIds;
    private List<Long> selectedComplementaireIds;

    public DossierCreationDTO() {}

    public Long getDemandeId() { return demandeId; }
    public void setDemandeId(Long demandeId) { this.demandeId = demandeId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<Long> getSelectedCommuneIds() { return selectedCommuneIds; }
    public void setSelectedCommuneIds(List<Long> selectedCommuneIds) { this.selectedCommuneIds = selectedCommuneIds; }

    public List<Long> getSelectedComplementaireIds() { return selectedComplementaireIds; }
    public void setSelectedComplementaireIds(List<Long> selectedComplementaireIds) { this.selectedComplementaireIds = selectedComplementaireIds; }
}
