package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    public Optional<Driver> findByDocument(String driver);
}
