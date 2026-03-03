package io.github.mgrablo.BiblioNode.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import io.github.mgrablo.BiblioNode.config.SecurityProperties;
import io.github.mgrablo.BiblioNode.dto.*;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
	private final UserService userService;
	private final UserRepository userRepository;
	private final ReaderService readerService;
	private final PasswordEncoder passwordEncoder;
	private final JwtEncoder jwtEncoder;
	private final SecurityProperties securityProperties;

	@Override
	public ReaderResponse register(RegisterRequest request) {
		User user = userService.createAccount(request.email(), request.password());
		ReaderRequest readerRequest = new ReaderRequest(request.fullName());

		return readerService.createProfile(readerRequest, user);
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		Instant now = Instant.now();
		String scope = user.getRoles().stream()
				.map(role -> role.getName().name())
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("BiblioNode")
				.issuedAt(now)
				.expiresAt(now.plus(securityProperties.jwtExpirationHours(), ChronoUnit.HOURS))
				.subject(user.getEmail())
				.claim("roles", scope)
				.build();

		String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

		return new LoginResponse(
				token,
				user.getEmail(),
				user.getRoles().stream().map(role -> role.getName().name()).toList()
		);
	}
}
