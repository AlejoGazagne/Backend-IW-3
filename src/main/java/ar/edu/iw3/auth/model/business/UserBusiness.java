package ar.edu.iw3.auth.model.business;

import java.util.*;
import java.util.stream.Collectors;

import ar.edu.iw3.auth.Role;
import ar.edu.iw3.auth.User;
import ar.edu.iw3.auth.model.business.exceptions.BadPasswordException;
import ar.edu.iw3.auth.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.auth.model.persistence.RoleRepository;
import ar.edu.iw3.auth.model.persistence.UserRepository;
import ar.edu.iw3.model.business.exceptions.FoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserBusiness implements IUserBusiness {

	@Autowired
	private UserRepository userDAO;

	@Autowired
	private RoleRepository roleDAO;

	@Override
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException {
		Optional<User> ou;
		try {
			ou = userDAO.findOneByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ou.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el usuari@ email o nombre =" + usernameOrEmail)
					.build();
		}
		return ou.get();
	}

	@Override
	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		if (!pEncoder.matches(oldPassword, user.getPassword())) {
			throw BadPasswordException.builder().build();
		}
		user.setPassword(pEncoder.encode(newPassword));
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, false);
	}

	@Override
	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, true);
	}

	private void setDisable(String usernameOrEmail, boolean enable) throws NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		user.setEnabled(enable);
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public List<Map<String, Object>> getAdminsAndOperators() throws BusinessException{
		try {
			List<User> users = userDAO.findAdminAndOperator();
			return users.stream().map(user -> {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("id", user.getIdUser());
				userMap.put("enabled", user.isEnabled());
				userMap.put("email", user.getEmail());
				userMap.put("username", user.getUsername());
				userMap.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
				return userMap;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void editUser(JsonNode jsonNode) throws BusinessException {
		try {
			Long userId = jsonNode.get("id").asLong();
			Optional<User> user = userDAO.findById(userId);

			if(user.isPresent()){
				if (jsonNode.has("enabled")) {
					user.get().setEnabled(jsonNode.get("enabled").asBoolean());
				}
				if (jsonNode.has("mail")) {
					user.get().setEmail(jsonNode.get("mail").asText());
				}
				if (jsonNode.has("name")) {
					user.get().setUsername(jsonNode.get("name").asText());
				}
				if (jsonNode.has("roles")) {
					Set<Role> roles = new HashSet<>();
					jsonNode.get("roles").forEach(roleNode -> {
						String roleName = roleNode.asText();
                        Role role = null;
                        try {
                            role = roleDAO.findByName(roleName)
                                    .orElseThrow(() -> new BusinessException("Role not found: " + roleName));
                        } catch (BusinessException e) {
                            throw new RuntimeException(e);
                        }
                        roles.add(role);
					});
					user.get().setRoles(roles);
				}
				userDAO.save(user.get());
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Autowired
	@Lazy
	private PasswordEncoder pEncoder;

	public void createUser(JsonNode jsonNode) throws BusinessException {
		System.out.println(jsonNode);
		try {
			String username = jsonNode.get("username").asText();
			String email = jsonNode.get("email").asText();
			String password = jsonNode.get("password").asText();
			boolean enabled = jsonNode.get("enabled").asBoolean();

			if(userDAO.findOneByUsernameOrEmail(username, email).isPresent()) {
				throw FoundException.builder().message("User already exists").build();
			}

			Set<Role> roles = new HashSet<>();
			jsonNode.get("roles").forEach(roleNode -> {
				String roleName = roleNode.asText();
                Role role = null;
                try {
                    role = roleDAO.findByName(roleName)
                            .orElseThrow(() -> new BusinessException("Role not found: " + roleName));
                } catch (BusinessException e) {
                    throw new RuntimeException(e);
                }
                roles.add(role);
			});

			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(pEncoder.encode(password));
			user.setRoles(roles);
			user.setEnabled(enabled);

			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}


}

