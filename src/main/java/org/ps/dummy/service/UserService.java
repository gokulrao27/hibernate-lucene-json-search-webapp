package org.ps.dummy.service;

import org.ps.dummy.dto.ExternalUserDTO;
import org.ps.dummy.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveAll(List<UserEntity> users);

    Page<UserEntity> search(String text, Pageable pageable);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> fetchExternalUsers();

    List<UserEntity> fallbackForExternalFetch(Throwable t);
}

