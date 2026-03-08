package io.github.mgrablo.BiblioNode.controller;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.LoginRequest;
import io.github.mgrablo.BiblioNode.dto.LoginResponse;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;
import io.github.mgrablo.BiblioNode.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RsaKeyConfig rsaKeyConfig;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    void register_ShouldBePublic() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new ReaderResponse(1L, "New Reader", "user@example.com", Collections.emptyList()));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"password\",\"fullName\":\"New Reader\"}")
        ).andExpect(status().isCreated());
    }

    @Test
    void login_ShouldBePublic() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("token", "user@example.com", java.util.Collections.singletonList("ROLE_READER")));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"password\"}")
        ).andExpect(status().isOk());
    }
}


