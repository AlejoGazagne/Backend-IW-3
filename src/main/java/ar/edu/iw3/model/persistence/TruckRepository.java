package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck, Long> {
}
