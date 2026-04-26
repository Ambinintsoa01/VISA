package mg.visa.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.TypeVisa;

public interface TypeVisaRepository extends JpaRepository<TypeVisa, Long> {
	java.util.Optional<TypeVisa> findByCode(String code);
}
