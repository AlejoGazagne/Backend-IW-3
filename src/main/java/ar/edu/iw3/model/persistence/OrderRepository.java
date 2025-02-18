package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select case when count(o) > 0 then true else false end from Order o where o.password = :password")
    public boolean existsByPassword(@Param("password") Integer pass);

//    @Query(value = "SELECT * FROM Order WHERE Order.state = :state", nativeQuery = true)
//    public List<Order> findByState(@Param("state") int state);

    Page<Order> findAllByStateOrderByDateReceivedDesc(Order.State state, Pageable pageable);

    Page<Order> findAllByOrderByDateReceivedDesc(Pageable pageable);

    public Optional<Order> findByPassword(Integer password);

    public Optional<Order> findByExternalId(String externalId) throws NotFoundException;

    public void deleteByExternalId(String externalId) throws NotFoundException;

    public Optional<Order> findById(long id) throws NotFoundException;

    @Query("select count(o) from Order o where o.state = :state")
    public int countOrderByState(@Param("state") Order.State state);

    @Query("select count(o) from Order o where month(o.dateReceived) = :month")
    public Integer countOrderByDateReceived(@Param("month") int month);

    @Query("select count(o) from Order o where month(o.dateReceived) = :month and o.dateReceived >= :oneYearAgo")
    public Integer countOrderByDateReceivedAndYear(@Param("month") int month, @Param("oneYearAgo") LocalDateTime oneYearAgo);

    @Query(value = "SELECT p.name AS productName, COUNT(o.id) AS orderCount " +
            "FROM products p " +
            "LEFT JOIN orders o ON p.id = o.id_product " +
            "GROUP BY p.id, p.name " +
            "ORDER BY orderCount DESC",
            nativeQuery = true)
    List<Object[]> findProductOrderCounts();

    @Query(value = "SELECT c.company_name AS clientName, COUNT(o.id) AS orderCount " +
            "FROM client c " +
            "LEFT JOIN orders o ON c.id = o.id_client " +
            "GROUP BY c.id, c.company_name " +
            "ORDER BY orderCount DESC" ,
            nativeQuery = true)
    List<Object[]> findClientOrderCounts();

    Page<Order> findAllByState(Order.State state, Pageable pageable);

    //Page<Order> getOrders(Pageable pageable);
}
