package com.carddex.sims2.security.controller;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.carddex.sims2.preferences.ResponseConstants;
import com.carddex.sims2.rest.dto.DepartmentsResp;
import com.carddex.sims2.rest.dto.PrivilegeResponse;
import com.carddex.sims2.rest.dto.RoleDto;
import com.carddex.sims2.rest.dto.StatusResponse;
import com.carddex.sims2.rest.dto.ZonesResp;
import com.carddex.sims2.security.JwtAuthenticationRequest;
import com.carddex.sims2.security.JwtTokenUtil;
import com.carddex.sims2.security.JwtUser;
import com.carddex.sims2.security.SUser;
import com.carddex.sims2.service.DeparmentService;
import com.carddex.sims2.service.JwtAuthenticationResponse;
import com.carddex.sims2.service.PrivilegeService;
import com.carddex.sims2.service.RoleService;
import com.carddex.sims2.service.UserAuthResponse;
import com.carddex.sims2.service.ZoneService;

@RestController
public class AuthenticationRestController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private DeparmentService departmentService;

	@Autowired
	private ZoneService zoneService;

	@RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest)
			throws AuthenticationException {

		try {

			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserAuthResponse(
					ResponseConstants.RESPONSE_ERROR, ResponseConstants.RESPONSE_DESCRIPTION_ERROR));
		} catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserAuthResponse(
					ResponseConstants.RESPONSE_ERROR, ResponseConstants.RESPONSE_DESCRIPTION_ERROR));
		}

		SUser userDetails = (SUser) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new UserAuthResponse(token, userDetails));
	}

	@RequestMapping(value = "/core/api/sys/user", method = RequestMethod.GET)
	public ResponseEntity<?> getUserInfo(HttpServletRequest request) throws AuthenticationException {

		try {

			String token = request.getHeader(tokenHeader).substring(7);
			String userName = jwtTokenUtil.getUsernameFromToken(token);

			if (userName != null) {
				SUser userDetails = (SUser) userDetailsService.loadUserByUsername(userName);
				return ResponseEntity.ok(new UserAuthResponse(token, userDetails));
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}

	}

	@RequestMapping(value = "/core/api/logout", method = RequestMethod.GET)
	public ResponseEntity<?> logout(HttpServletRequest request) throws AuthenticationException {

		try {

			String token = request.getHeader(tokenHeader).substring(7);
			String userName = jwtTokenUtil.getUsernameFromToken(token);

			if (userName != null) {
				SUser userDetails = (SUser) userDetailsService.loadUserByUsername(userName);
				return ResponseEntity.ok(new UserAuthResponse(token, userDetails));
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}

	}

	@RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String authToken = request.getHeader(tokenHeader);
		final String token = authToken.substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@RequestMapping(value = "/core/api/authorized", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> authorized(HttpServletRequest request) {
		String authToken = request.getHeader(tokenHeader);
		final String token = authToken.substring(7);

		try {
			if (jwtTokenUtil.getUsernameFromToken(token) != null) {
				return ResponseEntity.ok(new StatusResponse("success"));
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}

	}

	@RequestMapping(value = "/core/api/permissions", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getPrivileges(HttpServletRequest request) {
		List<PrivilegeResponse> list = privilegeService.getPrivilegeResponseList();
		return ResponseEntity.ok(list);
	}

	@RequestMapping(value = "/core/api/roles", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getRoles(HttpServletRequest request) {

		return ResponseEntity.ok(roleService.findAll());
	}

	@RequestMapping(value = "/core/api/roles/permissions", method = RequestMethod.POST)
	public ResponseEntity<Object> setRolePermissions(@RequestBody RoleDto role) {

		try {
			roleService.setRolePermissions(role);
			return ResponseEntity.ok(new StatusResponse("success"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}
	}

	@RequestMapping(value = "personnel/api/departments", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<DepartmentsResp> getDepartments() {

		return ResponseEntity.ok(departmentService.loadAlldepartments());
	}

	@RequestMapping(value = "/zones/api/zones", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ZonesResp> getZones() {

		ZonesResp result = zoneService.loadAll();
		return ResponseEntity.ok(result);
	}

	@ExceptionHandler({ AuthenticationException.class })
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	private void authenticate(String username, String password) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new AuthenticationException("User is disabled!", e);
		} catch (BadCredentialsException e) {
			throw new AuthenticationException("Bad credentials!", e);
		}
	}
}
