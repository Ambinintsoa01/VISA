package mg.visa.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.StatutDossier;

public interface StatutDossierRepository extends JpaRepository<StatutDossier, Long> {
    StatutDossier findByCode(String code);
}
