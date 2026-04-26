package mg.visa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.CataloguePieceComplementaire;

public interface CataloguePieceComplementaireRepository extends JpaRepository<CataloguePieceComplementaire, Long> {
	List<CataloguePieceComplementaire> findByTypeVisaId(Long typeVisaId);
	List<CataloguePieceComplementaire> findByTypeVisaIdAndObligatoireTrue(Long typeVisaId);

	// Find by the code of the associated TypeVisa (maps from TypeDemande.code)
	List<CataloguePieceComplementaire> findByTypeVisaCode(String typeVisaCode);
	List<CataloguePieceComplementaire> findByTypeVisaCodeAndObligatoireTrue(String typeVisaCode);

}
