package ar.edu.iw3.auth.model.business.interfaces;

import ar.edu.iw3.auth.User;
import ar.edu.iw3.auth.model.business.exceptions.BadPasswordException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

public interface IUserBusiness {
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void changePassword(long id, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException;

	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException;

	public List<Map<String, Object>>  getAdminsAndOperators() throws BusinessException;

	public void editUser(JsonNode jsonNode) throws BusinessException;

	public void createUser(JsonNode jsonNode) throws BusinessException, FoundException;

	public Map<String, Object> changeUser(JsonNode jsonNode) throws BusinessException, NotFoundException, BadPasswordException;
}
