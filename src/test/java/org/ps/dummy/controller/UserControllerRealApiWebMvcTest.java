package org.ps.dummy.controller;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ps.dummy.entity.UserEntity;
import org.ps.dummy.mapper.UserMapper;
import org.ps.dummy.repository.UserRepository;
import org.ps.dummy.service.UserService;
import org.ps.dummy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = org.ps.dummy.controller.UserController.class)
@Import(UserControllerRealApiWebMvcTest.TestConfig.class)
public class UserControllerRealApiWebMvcTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public UserMapper userMapper() {
            return new UserMapper();
        }

        @Bean
        public UserService userService(UserRepository repo, RestTemplate restTemplate, EntityManager em, UserMapper mapper) {
            // Use the real external API as requested
            String externalUrl = "https://dummyjson.com/users";
            return new UserServiceImpl(repo, restTemplate, em, mapper, externalUrl);
        }
    }

    @Autowired
    private UserService userService; // real UserServiceImpl created in TestConfig

    @MockBean
    private UserRepository userRepository; // mocked to verify saveAll

    @MockBean
    private EntityManager entityManager; // mocked to avoid Hibernate Search mass indexing

    @Test
    void fetchExternalUsers_callsRealApi_andPersists() {
        // make entityManager.unwrap throw to skip mass indexing step in saveAll
        when(entityManager.unwrap(Session.class)).thenThrow(new RuntimeException("no session"));

        // make saveAll return the list it was given
        when(userRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<UserEntity> res = (List<UserEntity>) userService.fetchExternalUsers();

        assertNotNull(res);
        assertFalse(res.isEmpty(), "Expected the real external API to return users");
        // dummyjson returns 30 users for /users
        assertEquals(30, res.size(), "Expected 30 users from dummyjson.com/users");
        assertTrue(res.stream().allMatch(u -> u.getId() != null), "All fetched users should have non-null id");

        verify(userRepository, times(1)).saveAll(anyList());
    }
}
