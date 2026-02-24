package io.github.mgrablo.BiblioNode.controller;

import io.github.mgrablo.BiblioNode.config.RsaKeyConfig;
import io.github.mgrablo.BiblioNode.config.SecurityConfiguration;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private RsaKeyConfig rsaKeyConfig;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void addBook_ShouldForbidReader() throws Exception {
        mockMvc.perform(post("/api/books")
                .with(user("reader").roles("READER"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"title\":\"Title\",\"isbn\":\"123\",\"authorId\":1}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void addBook_ShouldAllowAdmin() throws Exception {
        mockMvc.perform(post("/api/books")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"title\":\"Title\",\"isbn\":\"123\",\"authorId\":1}")
        ).andExpect(status().isCreated());
    }

    @Test
    void getAll_ShouldAllowReader() throws Exception {
        when(bookService.getAllBooks(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/books")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_ShouldAllowAdmin() throws Exception {
        when(bookService.getAllBooks(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/books")
                .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }

    @Test
    void getById_ShouldAllowReader() throws Exception {
        when(bookService.findBookById(1L))
                .thenReturn(new BookResponse(1L, "Title", "123", "Author", 1L, true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(get("/api/books/1")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void findByTitle_ShouldAllowReader() throws Exception {
        when(bookService.findBookByTitle(any(String.class)))
                .thenReturn(new BookResponse(1L, "Title", "123", "Author", 1L, true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(get("/api/books/find")
                .param("bookTitle", "Title")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void searchBooks_ShouldAllowReader() throws Exception {
        when(bookService.searchBooks(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/books/search")
                .param("bookTitle", "Title")
                .with(user("reader").roles("READER"))
        ).andExpect(status().isOk());
    }

    @Test
    void updateBook_ShouldAllowAdmin() throws Exception {
        when(bookService.updateBook(any(Long.class), any(io.github.mgrablo.BiblioNode.dto.BookRequest.class)))
                .thenReturn(new BookResponse(1L, "Title", "123", "Author", 1L, true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now()));

        mockMvc.perform(put("/api/books/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"title\":\"Title\",\"isbn\":\"123\",\"authorId\":1}")
        ).andExpect(status().isOk());
    }

    @Test
    void updateBook_ShouldForbidReader() throws Exception {
        mockMvc.perform(put("/api/books/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"title\":\"Title\",\"isbn\":\"123\",\"authorId\":1}")
        ).andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_ShouldAllowAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_ShouldForbidReader() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                .with(user("reader").roles("READER"))
                .with(csrf())
        ).andExpect(status().isForbidden());
    }
}
