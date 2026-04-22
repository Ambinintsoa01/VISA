package mg.visa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.DossierPieceCommune;

public interface DossierPieceCommuneRepository extends JpaRepository<DossierPieceCommune, Long> {
    List<DossierPieceCommune> findByDossierId(Long dossierId);
}
