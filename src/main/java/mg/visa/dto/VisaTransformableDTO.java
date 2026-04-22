package mg.visa.dto;

public class VisaTransformableDTO {
    private Long id;
    private Long passeportId;
    private Long typeVisaId;
    private String infos; // JSON string
    private String remarque;

    public VisaTransformableDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPasseportId() { return passeportId; }
    public void setPasseportId(Long passeportId) { this.passeportId = passeportId; }

    public Long getTypeVisaId() { return typeVisaId; }
    public void setTypeVisaId(Long typeVisaId) { this.typeVisaId = typeVisaId; }

    public String getInfos() { return infos; }
    public void setInfos(String infos) { this.infos = infos; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
