package mg.visa.dto;

import java.util.List;
import mg.visa.entity.Dossier;

public class DossierCreationResult {
    private Dossier dossier;
    private List<String> missingPieces;

    public DossierCreationResult() {}

    public DossierCreationResult(Dossier dossier, List<String> missingPieces) {
        this.dossier = dossier;
        this.missingPieces = missingPieces;
    }

    public Dossier getDossier() { return dossier; }
    public void setDossier(Dossier dossier) { this.dossier = dossier; }

    public List<String> getMissingPieces() { return missingPieces; }
    public void setMissingPieces(List<String> missingPieces) { this.missingPieces = missingPieces; }
}
