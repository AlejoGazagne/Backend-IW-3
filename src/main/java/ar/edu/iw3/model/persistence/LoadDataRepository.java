package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.LoadData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadDataRepository extends JpaRepository<LoadData, Long> {
}
