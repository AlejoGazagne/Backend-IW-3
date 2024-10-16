package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
