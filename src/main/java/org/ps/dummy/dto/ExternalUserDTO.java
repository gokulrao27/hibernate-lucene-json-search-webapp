package org.ps.dummy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String maidenName;
    private String email;
    private String ssn;
    private String phone;
    private String username;
    private Integer age;
    private String gender;
    private String birthDate;
    private String image;
    private String bloodGroup;
    private Double height;
    private Double weight;
    private String eyeColor;
    private HairDTO hair;
    private String ip;
    private AddressDTO address;
    private String macAddress;
    private String university;
    private BankDTO bank;
    private CompanyDTO company;
    private String ein;
    private String userAgent;
    private CryptoDTO crypto;
    private String role;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HairDTO {
        private String color;
        private String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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
        private CoordinatesDTO coordinates;
        private String country;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoordinatesDTO {
        private Double lat;
        private Double lng;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyDTO {
        private String department;
        private String name;
        private String title;
        private AddressDTO address;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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
