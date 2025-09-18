package org.ps.dummy.mapper;

import org.junit.jupiter.api.Test;
import org.ps.dummy.dto.ExternalUserDTO;
import org.ps.dummy.dto.ExternalUserDTO.AddressDTO;
import org.ps.dummy.dto.ExternalUserDTO.BankDTO;
import org.ps.dummy.dto.ExternalUserDTO.CompanyDTO;
import org.ps.dummy.dto.ExternalUserDTO.CryptoDTO;
import org.ps.dummy.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_fullMappingAndSsnFallback() {
        ExternalUserDTO dto = ExternalUserDTO.builder()
                .id(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .ssn(null) // missing ssn should be replaced
                .username("jdoe")
                .age(30)
                .address(AddressDTO.builder().address("1 Main St").city("Town").country("Country").build())
                .bank(BankDTO.builder().cardNumber("4111").cardType("VISA").currency("USD").build())
                .company(CompanyDTO.builder().name("Acme").department("Eng").title("Dev").build())
                .crypto(CryptoDTO.builder().coin("BTC").wallet("w").network("net").build())
                .build();

        UserEntity e = mapper.toEntity(dto);
        assertNotNull(e);
        assertEquals(123L, e.getId());
        assertEquals("John", e.getFirstName());
        assertEquals("Doe", e.getLastName());
        assertEquals("john@example.com", e.getEmail());
        // when ssn absent, mapper should produce a placeholder starting with N/A-
        assertNotNull(e.getSsn());
        assertTrue(e.getSsn().startsWith("N/A-"));
        assertEquals("jdoe", e.getUsername());
        assertEquals(30, e.getAge());
        assertNotNull(e.getAddress());
        assertEquals("1 Main St", e.getAddress().getAddress());
        assertNotNull(e.getBank());
        assertEquals("4111", e.getBank().getCardNumber());
        assertNotNull(e.getCompany());
        assertEquals("Acme", e.getCompany().getName());
        assertNotNull(e.getCrypto());
        assertEquals("BTC", e.getCrypto().getCoin());
    }

    @Test
    void toDto_roundtripMinimal() {
        UserEntity e = new UserEntity(5L, "A", "B", "a@b.com", "SSN-1");
        e.setUsername("u");
        e.setAge(21);

        org.ps.dummy.dto.UserResponseDTO dto = mapper.toDto(e);
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("A", dto.getFirstName());
        assertEquals("B", dto.getLastName());
        assertEquals("a@b.com", dto.getEmail());
        assertEquals("SSN-1", dto.getSsn());
        assertEquals("u", dto.getUsername());
        assertEquals(21, dto.getAge());
    }
}

