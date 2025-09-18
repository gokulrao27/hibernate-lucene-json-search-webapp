package org.ps.dummy.service;

import org.junit.jupiter.api.Test;
import org.ps.dummy.dto.ExternalUserDTO;
import org.ps.dummy.dto.ExternalUserListDTO;
import org.ps.dummy.entity.UserEntity;
import org.ps.dummy.mapper.UserMapper;
import org.ps.dummy.repository.UserRepository;
import org.ps.dummy.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Test
    void fetchExternalUsers_success_invokesSaveAllAndReturnsMapped() {
        UserRepository userRepository = mock(UserRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        UserMapper mapper = new UserMapper();
        String externalUrl = "http://external/api";

        ExternalUserDTO ext = ExternalUserDTO.builder().id(10L).firstName("X").lastName("Y").build();
        ExternalUserListDTO listDTO = new ExternalUserListDTO(List.of(ext), 1, 0, 10);
        when(restTemplate.getForEntity(externalUrl, ExternalUserListDTO.class)).thenReturn(ResponseEntity.ok(listDTO));

        UserServiceImpl svc = spy(new UserServiceImpl(userRepository, restTemplate, entityManager, mapper, externalUrl));
        // avoid side-effects from saveAll (persistence/indexing)
        doNothing().when(svc).saveAll(anyList());

        List<UserEntity> res = svc.fetchExternalUsers();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(10L, res.get(0).getId());
        verify(svc, times(1)).saveAll(anyList());
    }

    @Test
    void fetchExternalUsers_emptyBody_returnsEmptyList() {
        UserRepository userRepository = mock(UserRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        UserMapper mapper = new UserMapper();
        String externalUrl = "http://external/api";

        when(restTemplate.getForEntity(externalUrl, ExternalUserListDTO.class)).thenReturn(ResponseEntity.ok(null));

        UserServiceImpl svc = spy(new UserServiceImpl(userRepository, restTemplate, entityManager, mapper, externalUrl));
        List<UserEntity> res = svc.fetchExternalUsers();
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(svc, never()).saveAll(anyList());
    }

    @Test
    void fallbackForExternalFetch_readsLocalResource_andInvokesSaveAll() {
        UserRepository userRepository = mock(UserRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        UserMapper mapper = new UserMapper();
        String externalUrl = "http://external/api";

        UserServiceImpl svc = spy(new UserServiceImpl(userRepository, restTemplate, entityManager, mapper, externalUrl));
        doNothing().when(svc).saveAll(anyList());

        List<UserEntity> res = svc.fallbackForExternalFetch(new RuntimeException("boom"));
        // the project includes a users-payload.json resource; fallback should parse it
        assertNotNull(res);
        assertFalse(res.isEmpty(), "fallback should load users from classpath users-payload.json");
        verify(svc, times(1)).saveAll(anyList());
    }

    @Test
    void saveAll_handlesMassIndexingExceptions_andStillSaves() {
        UserRepository userRepository = mock(UserRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        UserMapper mapper = new UserMapper();
        String externalUrl = "http://external/api";

        // make entityManager.unwrap throw to exercise the exception handling path
        when(entityManager.unwrap(org.hibernate.Session.class)).thenThrow(new RuntimeException("no session"));

        UserServiceImpl svc = new UserServiceImpl(userRepository, restTemplate, entityManager, mapper, externalUrl);

        List<UserEntity> users = List.of(new UserEntity(1L, "A", "B", null, null));

        // should not throw despite mass indexing failure
        assertDoesNotThrow(() -> svc.saveAll(users));

        verify(userRepository, times(1)).saveAll(users);
    }
}
