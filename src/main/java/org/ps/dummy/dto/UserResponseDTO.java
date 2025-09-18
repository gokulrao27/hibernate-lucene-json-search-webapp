package org.ps.dummy.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String maidenName;
    private String email;
    private String ssn;
    private String username;
    private String phone;
    private Integer age;
    private String gender;
    private String birthDate;
    private String image;
    private String bloodGroup;
    private Double height;
    private Double weight;
    private String eyeColor;
    private String ip;
    private String macAddress;
    private String university;
    private String ein;
    private String userAgent;
    private String role;

    private AddressDTO address;
    private BankDTO bank;
    private CompanyDTO company;
    private CryptoDTO crypto;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDTO {
        private String address;
        private String city;
        private String state;
        private String stateCode;
        private String postalCode;
        private Double lat;
        private Double lng;
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankDTO {
        private String cardExpire;
        private String cardNumber;
        private String cardType;
        private String currency;
        private String iban;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyDTO {
        private String department;
        private String name;
        private String title;
        private String companyAddress;
        private String companyCity;
        private String companyState;
        private String companyStateCode;
        private String companyPostalCode;
        private String companyCountry;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CryptoDTO {
        private String coin;
        private String wallet;
        private String network;
    }
}
