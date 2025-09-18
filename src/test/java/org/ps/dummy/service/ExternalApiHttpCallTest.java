package org.ps.dummy.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ps.dummy.dto.ExternalUserListDTO;
import org.ps.dummy.entity.UserEntity;
import org.ps.dummy.mapper.UserMapper;
import org.ps.dummy.repository.UserRepository;
import org.ps.dummy.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class ExternalApiHttpCallTest {

    private static HttpServer server;
    private static byte[] payloadBytes;

    @BeforeAll
    static void startServer() throws Exception {
        try (var is = ExternalApiHttpCallTest.class.getClassLoader().getResourceAsStream("users-payload.json")) {
            if (is == null) throw new IllegalStateException("users-payload.json not found on classpath");
            payloadBytes = is.readAllBytes();
        }
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/external/users", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, payloadBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payloadBytes);
            }
        });
        server.start();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void realHttpCall_fetchesAndSaves() {
        String url = "http://localhost:" + server.getAddress().getPort() + "/external/users";

        UserRepository repo = mock(UserRepository.class);
        EntityManager em = mock(EntityManager.class);
        RestTemplate restTemplate = new RestTemplate();
        UserMapper mapper = new UserMapper();

        // emulate saveAll returning the same list
        when(repo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        // make unwrap throw to skip mass indexing
        when(em.unwrap(org.hibernate.Session.class)).thenThrow(new RuntimeException("no session"));

        UserServiceImpl svc = new UserServiceImpl(repo, restTemplate, em, mapper, url);

        List<UserEntity> res = svc.fetchExternalUsers();

        assertNotNull(res);
        assertFalse(res.isEmpty(), "Expected fetched users from real HTTP call");
        verify(repo, times(1)).saveAll(anyList());

        // verify returned entities have ids (from payload)
        assertTrue(res.stream().allMatch(e -> e.getId() != null));
    }
}

