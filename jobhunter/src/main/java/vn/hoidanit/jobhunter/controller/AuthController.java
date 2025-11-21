package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDto;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private SecurityUtil securityUtil;
	private UserService userService;
	
	@Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
	private long refreshTokenExpiration;

	public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,UserService userService) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.securityUtil = securityUtil;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<ResLoginDto> login(@Valid @RequestBody LoginDTO loginDTO) {
		// Nạp input gồm username/password vào Security
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDTO.getName(), loginDTO.getPassword());

		// xác thực người dùng => cần viết hàm loadUserByUsername
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// create
		String access_token = securityUtil.createAccessToken(authentication);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User currentUser = this.userService.handleGetUserByEmail(loginDTO.getName());
		ResLoginDto res=  new ResLoginDto();
		res.setAccessToken(access_token);
		ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName());
		res.setUser(userLogin);
		// create Refresh token
		String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getName(), res);
		// update user
		this.userService.updateUserToken(refreshToken, loginDTO.getName());
		// set cookie
		ResponseCookie resCookies = ResponseCookie.from("refresh_token",refreshToken)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(refreshTokenExpiration)
				.build();
		
		
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, resCookies.toString())
				.body(res);
	}

}
