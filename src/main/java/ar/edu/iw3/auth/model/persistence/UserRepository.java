package ar.edu.iw3.auth.model.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.iw3.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	public Optional<User> findOneByUsernameOrEmail(String username, String email);

	@Query(value = "SELECT DISTINCT u.* FROM users u " +
			"JOIN userroles ur ON u.id_user = ur.id_user " +
			"JOIN roles r ON ur.id_role = r.id " +
			"WHERE r.name IN ('ROLE_ADMIN', 'ROLE_OPERATOR')", nativeQuery = true)
	public List<User> findAdminAndOperator();
}
