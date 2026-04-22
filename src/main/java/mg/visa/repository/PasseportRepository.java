package mg.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.visa.entity.Passeport;

public interface PasseportRepository extends JpaRepository<Passeport, Long> {
}
