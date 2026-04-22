package mg.visa.dto;

public class UploadResponseDTO {
    private String path;

    public UploadResponseDTO() {}

    public UploadResponseDTO(String path) { this.path = path; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
