package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select case when count(o) > 0 then true else false end from Order o where o.password = :password")
    public boolean existsByPassword(@Param("password") Integer pass);

    @Query(value = "SELECT * FROM Order WHERE Order.state = :state", nativeQuery = true)
    public List<Order> findByState(@Param("state") int state);
}
