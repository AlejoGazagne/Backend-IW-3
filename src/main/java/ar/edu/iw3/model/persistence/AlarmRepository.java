package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository  extends JpaRepository<Alarm, Long> {

    Optional<List<Alarm>> findByStatus(Alarm.State status);
}