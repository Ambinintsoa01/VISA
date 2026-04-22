package mg.visa.repository.ref;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.StatutPiece;

public interface StatutPieceRepository extends JpaRepository<StatutPiece, Long> {
    Optional<StatutPiece> findByCode(String code);
}
