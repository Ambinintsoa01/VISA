package mg.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.Demandeur;

public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
}
