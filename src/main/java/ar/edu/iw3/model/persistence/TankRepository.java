package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Tank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TankRepository extends JpaRepository<Tank, Long> {
}
