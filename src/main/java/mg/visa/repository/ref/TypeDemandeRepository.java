package mg.visa.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.TypeDemande;

public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Long> {
    java.util.Optional<TypeDemande> findByCode(String code);
}
