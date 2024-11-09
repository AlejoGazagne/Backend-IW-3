package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    //public Optional<Client> findByCompanyName(String client);

    public Optional<Client> findByExternalId(String externalId);
}
