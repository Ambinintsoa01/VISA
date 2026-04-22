package mg.visa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.DossierPieceComplementaire;

public interface DossierPieceComplementaireRepository extends JpaRepository<DossierPieceComplementaire, Long> {
    List<DossierPieceComplementaire> findByDossierId(Long dossierId);
}
