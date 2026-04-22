package mg.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.Demande;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
}
