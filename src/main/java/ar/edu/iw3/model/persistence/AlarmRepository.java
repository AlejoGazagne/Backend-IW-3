package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Alarm.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Optional<List<Alarm>> findByStatus(Alarm.State status);

    Optional<Alarm> findByStatusAndOrder_Id(Alarm.State status, Long orderId);
  
    Page<Alarm> findByStatus(Pageable pageable, Alarm.State status);

    Object countByStatus(Alarm.State status);

    @Query("select count(a) from Alarm a where month(a.dateOccurrence) = :month and a.status = :status")
    Integer countByStatusAndMonth(@Param("status") Alarm.State status, @Param("month") int month);

    @Query("select count(a) from Alarm a where month(a.dateOccurrence) = :month and a.order.product.id = :productId")
    Integer countByProductAndMonth(@Param("productId") Long productId, @Param("month") int month);

}