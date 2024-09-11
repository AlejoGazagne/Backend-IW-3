package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByProvider(String provider);

    Optional<Provider> findByProviderAndIdNot(String provider, long id);
}
