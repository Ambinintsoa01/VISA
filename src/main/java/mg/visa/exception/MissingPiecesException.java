package mg.visa.exception;

import java.util.List;

public class MissingPiecesException extends RuntimeException {
    private final List<String> missingPieces;

    public MissingPiecesException(List<String> missingPieces) {
        super("Missing mandatory pieces: " + String.join(",", missingPieces));
        this.missingPieces = missingPieces;
    }

    public List<String> getMissingPieces() { return missingPieces; }
}
