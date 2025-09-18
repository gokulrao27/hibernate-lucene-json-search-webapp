package org.ps.dummy.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.ps.dummy.dto.ExternalUserListDTO;
import org.ps.dummy.entity.UserEntity;
import org.ps.dummy.repository.UserRepository;
import org.ps.dummy.mapper.UserMapper;
import org.ps.dummy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final EntityManager entityManager;
    private final UserMapper mapper;
    private final String externalUrl;

    public UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate, EntityManager entityManager,
                           UserMapper mapper,
                           @Value("${app.external.users-url}") String externalUrl) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.entityManager = entityManager;
        this.mapper = mapper;
        this.externalUrl = externalUrl;
    }

    @Override
    public void saveAll(List<UserEntity> users) {
        log.info("Saving {} users to database", users.size());
        userRepository.saveAll(users);
        // Ensure persistence context flushed
        entityManager.flush();
        entityManager.clear();

        try {
            Session hibSession = entityManager.unwrap(org.hibernate.Session.class);
            Search.session(hibSession).massIndexer(UserEntity.class).startAndWait();
            log.debug("Hibernate Search mass indexing completed");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Mass indexing interrupted: {}", ie.toString());
        } catch (Exception ex) {
            log.warn("Mass indexing failed (continuing without search indexing): {}", ex.toString());
        }
    }

    @Override
    public Page<UserEntity> search(String text, Pageable pageable) {
        log.debug("Searching users by text='{}', page={}/{}", text, pageable.getPageNumber(), pageable.getPageSize());
        String query = (text == null || text.trim().isEmpty()) ? null : text.trim();
        if (query == null) {
            return userRepository.findAll(pageable);
        }

        Session hibSession = entityManager.unwrap(org.hibernate.Session.class);
        SearchSession searchSession = Search.session(hibSession);
        SearchResult<UserEntity> result = searchSession
                .search(UserEntity.class)
                .where(f -> f.bool(b -> b
                        .should(f.simpleQueryString().fields("firstName", "lastName", "ssn", "email", "username").matching(query))
                        // use contains wildcard to match substrings
                        .should(f.wildcard().fields("firstName", "lastName", "ssn", "email", "username").matching("*" + query + "*"))
                ))
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        List<UserEntity> hits = result.hits();
        long total = result.total().hitCount();
        return new PageImpl<>(hits, pageable, total);

    }


    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<UserEntity> findById(Long id) {
        log.debug("Finding user by id={}", id);
        return userRepository.findById(id);
    }

    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    public Optional<UserEntity> findByEmail(String email) {
        log.debug("Finding user by email={}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackForExternalFetch")
    @Retry(name = "externalApi")
    public List<UserEntity> fetchExternalUsers() {
        log.info("Fetching external users from {}", externalUrl);
        ResponseEntity<ExternalUserListDTO> resp = restTemplate.getForEntity(externalUrl, ExternalUserListDTO.class);
        ExternalUserListDTO dto = resp.getBody();
        if (dto == null || dto.getUsers() == null) {
            log.warn("External API returned empty body");
            return new ArrayList<>();
        }
        List<UserEntity> entities = dto.getUsers().stream().map(mapper::toEntity).collect(Collectors.toList());
        saveAll(entities);
        return entities;
    }

    @Override
    public List<UserEntity> fallbackForExternalFetch(Throwable t) {
        log.error("Fallback for external fetch triggered: {}", t.toString());
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("users-payload.json")) {
            if (is == null) {
                log.warn("Local users-payload.json not found on classpath");
                return new ArrayList<>();
            }
            com.fasterxml.jackson.databind.ObjectMapper jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            ExternalUserListDTO dto = jsonMapper.readValue(is, ExternalUserListDTO.class);
            if (dto == null || dto.getUsers() == null) return new ArrayList<>();
            List<UserEntity> entities = dto.getUsers().stream().map(this.mapper::toEntity).collect(Collectors.toList());
            saveAll(entities);
            return entities;
        } catch (Exception ex) {
            log.error("Failed to read local users-payload.json fallback: {}", ex.toString());
            return new ArrayList<>();
        }
    }
}
