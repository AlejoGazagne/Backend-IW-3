package ar.edu.iw3.auth.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import ar.edu.iw3.auth.User;
import ar.edu.iw3.auth.custom.CustomAuthenticationManager;
import ar.edu.iw3.auth.filters.AuthConstants;
import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.util.IStandartResponseBusiness;

@RestController
@Tag(description = "API de autenticación", name = "Auth")
public class AuthRestController extends BaseRestController {
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private IStandartResponseBusiness response;

	@Operation(operationId = "Login", summary = "Login de usuario")
	@Parameter(in = ParameterIn.PATH, name = "username", description = "Nombre de usuario", required = true)
	@Parameter(in = ParameterIn.PATH, name = "password", description = "Contraseña", required = true)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping(value = Constants.URL_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loginExternalOnlyToken(@RequestParam String username, @RequestParam String password) {
		Authentication auth = null;
		try {
			auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
		} catch (AuthenticationServiceException e0) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}

		User user = (User) auth.getPrincipal();
		String token = JWT.create().withSubject(user.getUsername())
				.withClaim("internalId", user.getIdUser())
				.withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr())).withClaim("email", user.getEmail())
				.withClaim("version", "1.0.0")
				.withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

		// Create a map to hold the response data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("id", user.getIdUser());
		responseData.put("username", user.getUsername());
		responseData.put("roles", user.getAuthoritiesStr());
		responseData.put("token", token);

		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}

	@Autowired
	private PasswordEncoder pEncoder;

	@Operation(operationId = "EncodePass", summary = "Codifica una contraseña")
	@Parameter(in = ParameterIn.PATH, name = "password", description = "Contraseña a codificar", required = true)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contraseña codificada", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping(value = "/demo/encodepass", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> encodepass(@RequestParam String password) {
		try {
			return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
