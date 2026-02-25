package io.github.mgrablo.BiblioNode.controller;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.BorrowRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.exception.GlobalExceptionHandler;
import io.github.mgrablo.BiblioNode.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class})
public class LoanControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private RsaKeyConfig rsaKeyConfig;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    void borrowBook_ShouldAllowReader() throws Exception {
        var response = new LoanResponse(1L, 1L, "Title", "Author", "123", 100L, LocalDateTime.now(), LocalDateTime.now().plusDays(14), null);
        when(loanService.borrowBook(any(BorrowRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/loans/borrow")
            .with(csrf())
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_READER")))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"bookId\":1}")
        ).andExpect(status().isCreated());
    }

    @Test
    void borrowBook_ShouldForbidAdmin() throws Exception {
        mockMvc.perform(post("/api/loans/borrow")
            .with(csrf())
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"bookId\":1}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void borrowBook_ShouldForbidUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/loans/borrow")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"bookId\":1}")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void returnBook_ShouldAllowAdmin() throws Exception {
        var response = new LoanResponse(1L, 1L, "Title", "Author", "123", 100L, LocalDateTime.now(), LocalDateTime.now().plusDays(14), LocalDateTime.now());
        when(loanService.returnBook(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/loans/1/return")
            .with(csrf())
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk());
    }

    @Test
    void returnBook_ShouldForbidReader() throws Exception {
        mockMvc.perform(patch("/api/loans/1/return")
            .with(csrf())
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_READER")))
        ).andExpect(status().isForbidden());
    }

    @Test
    void getLoans_ShouldAllowAdmin() throws Exception {
        when(loanService.getAllLoans(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/loans")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk());
    }

    @Test
    void getLoans_ShouldForbidReader() throws Exception {
        mockMvc.perform(get("/api/loans")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_READER")))
        ).andExpect(status().isForbidden());
    }

    @Test
    void getOverdueLoans_ShouldAllowAdmin() throws Exception {
        when(loanService.getOverdueLoans(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/loans/overdue")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk());
    }

    @Test
    void getOverdueLoans_ShouldForbidReader() throws Exception {
        mockMvc.perform(get("/api/loans/overdue")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_READER")))
        ).andExpect(status().isForbidden());
    }
}
