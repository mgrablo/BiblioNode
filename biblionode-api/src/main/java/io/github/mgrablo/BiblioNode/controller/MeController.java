package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.dto.UserProfileResponse;
import io.github.mgrablo.BiblioNode.service.LoanService;
import io.github.mgrablo.BiblioNode.service.ReaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Me", description = "Endpoints related to the authenticated user's profile and actions.")
public class MeController {
	private final ReaderService readerService;
	private final LoanService loanService;

	@GetMapping
	public ResponseEntity<UserProfileResponse> getProfile(
			@AuthenticationPrincipal Jwt jwt
	) {
		String email = jwt.getSubject();
		UserProfileResponse profile = readerService.getUserProfileByEmail(email);
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/loans")
	public ResponseEntity<Page<LoanResponse>> getMyLoans(
			@AuthenticationPrincipal Jwt jwt,
			@ParameterObject Pageable pageable,
			@RequestParam(defaultValue = "false") boolean activeOnly
	) {
		String email = jwt.getSubject();
		Page<LoanResponse> loans = loanService.getLoansByReaderEmail(email, pageable);
		return ResponseEntity.ok(loans);
	}
}
