package mg.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.TypeDemande;

public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Long> {
}
