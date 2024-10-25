package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
