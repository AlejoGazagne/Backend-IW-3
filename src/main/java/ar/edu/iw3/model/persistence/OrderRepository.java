package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
