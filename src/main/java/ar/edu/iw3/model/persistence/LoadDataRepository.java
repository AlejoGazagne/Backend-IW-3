package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.LoadData;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoadDataRepository extends JpaRepository<LoadData, Long> {

    public Optional<List<LoadData>> findByOrderId(long id);

    @Query("SELECT AVG(l.temperature) FROM LoadData l WHERE l.order.id = :orderId")
    Double avgTemperature(long orderId);

    @Query("SELECT AVG(l.density) FROM LoadData l WHERE l.order.id = :orderId")
    Double avgDensity(long orderId);

    @Query("SELECT AVG(l.caudal) FROM LoadData l WHERE l.order.id = :orderId")
    Double avgCaudal(long orderId);

    //List<LoadData> findByOrderId(long orderId);
}
