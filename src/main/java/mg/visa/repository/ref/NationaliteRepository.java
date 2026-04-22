package mg.visa.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.ref.Nationalite;

public interface NationaliteRepository extends JpaRepository<Nationalite, Long> {
}
