package ar.edu.iw3.auth.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ar.edu.iw3.auth.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import ar.edu.iw3.auth.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(AuthConstants.AUTH_HEADER_NAME);
		String param = req.getParameter(AuthConstants.AUTH_PARAM_NAME);
		boolean byHeader = !(header == null || !header.startsWith(AuthConstants.TOKEN_PREFIX));
		boolean byParam = !(param == null || param.trim().length() < 10);
		// Si no se envía o es correcto el inicio de la cabecera o bien no se envía un
		// parámetro correcto, se continúa con el resto de los filtros
		if (!byHeader && !byParam) {
			chain.doFilter(req, res);
			return;
		}
		// Le damos prioridad al header.
		UsernamePasswordAuthenticationToken authentication = getAuthentication(req, byHeader);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	// Extraer el token JWT de la cabecera y lo intenta validar
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, boolean byHeader) {
		// Recordar que el header inicia con alguna cadena, por ejemplo: 'Bearer '
		String token = byHeader
				? request.getHeader(AuthConstants.AUTH_HEADER_NAME).replace(AuthConstants.TOKEN_PREFIX, "")
				: request.getParameter(AuthConstants.AUTH_PARAM_NAME);

		if (token != null) {
			// Parseamos el token usando la librería
			DecodedJWT jwt = null;
			try {
				jwt = JWT.require(Algorithm.HMAC512(AuthConstants.SECRET.getBytes())).build().verify(token);
				log.trace("Token recibido por '{}'", byHeader ? "header" : "query param");
				log.trace("Usuario logueado: " + jwt.getSubject());
				log.trace("Roles: " + jwt.getClaim("roles"));
				log.trace("Custom JWT Version: " + jwt.getClaim("version").asString());

				Set<Role> roles = new HashSet<Role>();

				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				@SuppressWarnings("unchecked")
				List<String> rolesStr = (List<String>) jwt.getClaim("roles").as(List.class);
				authorities = rolesStr.stream().map(role -> new SimpleGrantedAuthority(role))
						.collect(Collectors.toList());
				roles = rolesStr.stream().map(role -> new Role(role, 0, role)).collect(Collectors.toSet());
				String username = jwt.getSubject();

				if (username != null) {
					User user = new User();
					user.setIdUser(jwt.getClaim("internalId").asLong());
					user.setUsername(username);
					user.setRoles(roles);
					user.setEmail(jwt.getClaim("email").asString());
					return new UsernamePasswordAuthenticationToken(user, null, authorities);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			return null;
		}
		return null;
	}
}