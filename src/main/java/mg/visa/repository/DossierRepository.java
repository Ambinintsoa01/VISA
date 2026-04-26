package mg.visa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.visa.entity.Dossier;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

    Optional<Dossier> findByDemandeId(Long demandeId);

    @Query(value = "SELECT 'COMMUNE' as piece_type, dpc.id as piece_id, dpc.catalogue_piece_commune_id as catalogue_id, dpc.statut_piece_id as statut_id, dpc.fichier_path as fichier_path FROM dossier_piece_commune dpc WHERE dpc.dossier_id = :id "
            + "UNION ALL "
            + "SELECT 'COMPLEMENTAIRE', dpcp.id, dpcp.catalogue_piece_complementaire_id, dpcp.statut_piece_id, dpcp.fichier_path FROM dossier_piece_complementaire dpcp WHERE dpcp.dossier_id = :id",
            nativeQuery = true)
    List<Object[]> findPiecesParDossier(@Param("id") Long id);
}
