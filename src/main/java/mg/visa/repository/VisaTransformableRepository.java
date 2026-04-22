package mg.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.VisaTransformable;

public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Long> {
}
