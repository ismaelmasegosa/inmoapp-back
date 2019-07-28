package com.inmoapp.coresecurity;

import static com.inmoapp.coresecurity.config.SecurityConstants.HEADER_STRING;
import static com.inmoapp.coresecurity.config.SecurityConstants.SECRET;
import static com.inmoapp.coresecurity.config.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	/**
	 * Constructor para la autenticacion
	 * 
	 * @param authenticationManager
	 *            AuthenticationManager
	 */
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(HEADER_STRING);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	/**
	 * Comprueba la autenticacion mediante el token
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return UsernamePasswordAuthenticationToken
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			String user = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody().getSubject();

			if (user != null) {
				usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null,
						new ArrayList<>());
			}

			return usernamePasswordAuthenticationToken;
		}
		return usernamePasswordAuthenticationToken;
	}
}
