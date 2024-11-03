package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.LoadData;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadDataRepository extends JpaRepository<LoadData, Long> {
        public Optional<List<LoadData>> findByOrderId(long id);

}
