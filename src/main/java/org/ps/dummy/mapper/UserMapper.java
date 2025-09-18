package org.ps.dummy.mapper;

import org.ps.dummy.dto.ExternalUserDTO;
import org.ps.dummy.dto.UserResponseDTO;
import org.ps.dummy.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(ExternalUserDTO dto) {
        if (dto == null) return null;
        UserEntity e = new UserEntity();
        e.setId(dto.getId());
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setMaidenName(dto.getMaidenName());
        e.setEmail(dto.getEmail());
        String ssn = dto.getSsn();
        if (ssn == null || ssn.isEmpty()) {
            ssn = "N/A-" + dto.getId();
        }
        e.setSsn(ssn);
        e.setUsername(dto.getUsername());
        e.setPhone(dto.getPhone());
        e.setAge(dto.getAge());
        e.setGender(dto.getGender());
        e.setBirthDate(dto.getBirthDate());
        e.setImage(dto.getImage());
        e.setBloodGroup(dto.getBloodGroup());
        e.setHeight(dto.getHeight());
        e.setWeight(dto.getWeight());
        e.setEyeColor(dto.getEyeColor());
        e.setIp(dto.getIp());
        e.setMacAddress(dto.getMacAddress());
        e.setUniversity(dto.getUniversity());
        e.setEin(dto.getEin());
        e.setUserAgent(dto.getUserAgent());
        e.setRole(dto.getRole());

        if (dto.getAddress() != null) {
            ExternalUserDTO.AddressDTO a = dto.getAddress();
            UserEntity.Address addr = new UserEntity.Address();
            addr.setAddress(a.getAddress());
            addr.setCity(a.getCity());
            addr.setState(a.getState());
            addr.setStateCode(a.getStateCode());
            addr.setPostalCode(a.getPostalCode());
            if (a.getCoordinates() != null) {
                addr.setLat(a.getCoordinates().getLat());
                addr.setLng(a.getCoordinates().getLng());
            }
            addr.setCountry(a.getCountry());
            e.setAddress(addr);
        }

        if (dto.getBank() != null) {
            ExternalUserDTO.BankDTO b = dto.getBank();
            UserEntity.Bank bank = new UserEntity.Bank();
            bank.setCardExpire(b.getCardExpire());
            bank.setCardNumber(b.getCardNumber());
            bank.setCardType(b.getCardType());
            bank.setCurrency(b.getCurrency());
            bank.setIban(b.getIban());
            e.setBank(bank);
        }

        if (dto.getCompany() != null) {
            ExternalUserDTO.CompanyDTO c = dto.getCompany();
            UserEntity.Company comp = new UserEntity.Company();
            comp.setDepartment(c.getDepartment());
            comp.setName(c.getName());
            comp.setTitle(c.getTitle());
            if (c.getAddress() != null) {
                ExternalUserDTO.AddressDTO ca = c.getAddress();
                comp.setCompanyAddress(ca.getAddress());
                comp.setCompanyCity(ca.getCity());
                comp.setCompanyState(ca.getState());
                comp.setCompanyStateCode(ca.getStateCode());
                comp.setCompanyPostalCode(ca.getPostalCode());
                comp.setCompanyCountry(ca.getCountry());
            }
            e.setCompany(comp);
        }

        if (dto.getCrypto() != null) {
            ExternalUserDTO.CryptoDTO cr = dto.getCrypto();
            UserEntity.Crypto crypto = new UserEntity.Crypto();
            crypto.setCoin(cr.getCoin());
            crypto.setWallet(cr.getWallet());
            crypto.setNetwork(cr.getNetwork());
            e.setCrypto(crypto);
        }

        return e;
    }

    public UserResponseDTO toDto(UserEntity e) {
        if (e == null) return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(e.getId());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setMaidenName(e.getMaidenName());
        dto.setEmail(e.getEmail());
        dto.setSsn(e.getSsn());
        dto.setUsername(e.getUsername());
        dto.setPhone(e.getPhone());
        dto.setAge(e.getAge());
        dto.setGender(e.getGender());
        dto.setBirthDate(e.getBirthDate());
        dto.setImage(e.getImage());
        dto.setBloodGroup(e.getBloodGroup());
        dto.setHeight(e.getHeight());
        dto.setWeight(e.getWeight());
        dto.setEyeColor(e.getEyeColor());
        dto.setIp(e.getIp());
        dto.setMacAddress(e.getMacAddress());
        dto.setUniversity(e.getUniversity());
        dto.setEin(e.getEin());
        dto.setUserAgent(e.getUserAgent());
        dto.setRole(e.getRole());

        if (e.getAddress() != null) {
            UserResponseDTO.AddressDTO a = new UserResponseDTO.AddressDTO();
            a.setAddress(e.getAddress().getAddress());
            a.setCity(e.getAddress().getCity());
            a.setState(e.getAddress().getState());
            a.setStateCode(e.getAddress().getStateCode());
            a.setPostalCode(e.getAddress().getPostalCode());
            a.setLat(e.getAddress().getLat());
            a.setLng(e.getAddress().getLng());
            a.setCountry(e.getAddress().getCountry());
            dto.setAddress(a);
        }

        if (e.getBank() != null) {
            UserResponseDTO.BankDTO b = new UserResponseDTO.BankDTO();
            b.setCardExpire(e.getBank().getCardExpire());
            b.setCardNumber(e.getBank().getCardNumber());
            b.setCardType(e.getBank().getCardType());
            b.setCurrency(e.getBank().getCurrency());
            b.setIban(e.getBank().getIban());
            dto.setBank(b);
        }

        if (e.getCompany() != null) {
            UserResponseDTO.CompanyDTO c = new UserResponseDTO.CompanyDTO();
            c.setDepartment(e.getCompany().getDepartment());
            c.setName(e.getCompany().getName());
            c.setTitle(e.getCompany().getTitle());
            c.setCompanyAddress(e.getCompany().getCompanyAddress());
            c.setCompanyCity(e.getCompany().getCompanyCity());
            c.setCompanyState(e.getCompany().getCompanyState());
            c.setCompanyStateCode(e.getCompany().getCompanyStateCode());
            c.setCompanyPostalCode(e.getCompany().getCompanyPostalCode());
            c.setCompanyCountry(e.getCompany().getCompanyCountry());
            dto.setCompany(c);
        }

        if (e.getCrypto() != null) {
            UserResponseDTO.CryptoDTO cr = new UserResponseDTO.CryptoDTO();
            cr.setCoin(e.getCrypto().getCoin());
            cr.setWallet(e.getCrypto().getWallet());
            cr.setNetwork(e.getCrypto().getNetwork());
            dto.setCrypto(cr);
        }

        return dto;
    }
}
