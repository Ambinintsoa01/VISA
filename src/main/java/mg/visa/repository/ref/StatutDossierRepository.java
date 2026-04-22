package mg.visa.repository.ref;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.StatutDossier;

public interface StatutDossierRepository extends JpaRepository<StatutDossier, Long> {
    Optional<StatutDossier> findByCode(String code);
}
