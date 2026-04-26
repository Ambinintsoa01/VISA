package mg.visa.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.StatutPiece;

public interface StatutPieceRepository extends JpaRepository<StatutPiece, Long> {
    StatutPiece findByCode(String code);
}
