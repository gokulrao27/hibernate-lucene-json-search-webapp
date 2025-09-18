package org.ps.dummy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.ps.dummy.dto.UserResponseDTO;
import org.ps.dummy.entity.UserEntity;
import org.ps.dummy.exception.ResourceNotFoundException;
import org.ps.dummy.mapper.UserMapper;
import org.ps.dummy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "Search users", description = "Search users by free text across firstName, lastName and ssn")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Paged list of users")})
    @GetMapping(value = {"", "/search"})
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @Parameter(description = "Free-text query (param 'q' or 'query')") @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") @PositiveOrZero int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "10") @Min(1) int size
    ) {
        String effective = q != null ? q : query;
        
        if (effective != null) {
            effective = effective.trim();
            if (effective.length() >= 2 && effective.startsWith("\"") && effective.endsWith("\"")) {
                effective = effective.substring(1, effective.length() - 1);
            }
        }
        logger.info("Received request to search users q='{}' (query='{}') page={} size={}", q, query, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> result = userService.search(effective, pageable);
        Page<UserResponseDTO> dtoPage = result.map(mapper::toDto);
        
        return ResponseEntity.ok(dtoPage.getContent());
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User found"), @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@Parameter(description = "User id") @PathVariable("id") Long id) {
        logger.info("Received request get user by id={}", id);
        UserEntity user = userService.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found id=" + id));
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Operation(summary = "Get user by email")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User found"), @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDTO> getByEmail(@Parameter(description = "Email to search") @RequestParam("email") @Email String email) {
        logger.info("Received request get user by email={}", email);
        UserEntity user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found email=" + email));
        return ResponseEntity.ok(mapper.toDto(user));
    }
}
