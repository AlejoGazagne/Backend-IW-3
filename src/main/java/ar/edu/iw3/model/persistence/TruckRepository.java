package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TruckRepository extends JpaRepository<Truck, Long> {
    //public Optional<Truck> findByPlate(String plate);

    public Optional<Truck> findByExternalId(String externalId);
}
