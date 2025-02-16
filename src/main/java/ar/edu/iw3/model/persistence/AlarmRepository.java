package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Alarm.State;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Optional<List<Alarm>> findByStatus(Alarm.State status);

    Optional<Alarm> findByStatusAndOrder_Id(Alarm.State status, Long orderId);
}