package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Tank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TankRepository extends JpaRepository<Tank, Long> {
    public Optional<Tank> findByExternalId(String externalId);
}
