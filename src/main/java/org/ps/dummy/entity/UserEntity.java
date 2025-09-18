package org.ps.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@Entity
@Table(name = "users")
@Indexed
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @FullTextField
    private String firstName;

    @FullTextField
    private String lastName;

    private String maidenName;

    @GenericField
    @Column(unique = true)
    private String email;

    @FullTextField
    @Column(length = 100)
    private String ssn;

    @FullTextField
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

    @Embedded
    @IndexedEmbedded
    private Address address;

    @Embedded
    @IndexedEmbedded
    private Bank bank;

    @Embedded
    @IndexedEmbedded
    private Company company;

    @Embedded
    @IndexedEmbedded
    private Crypto crypto;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private String address;
        private String city;
        private String state;
        private String stateCode;
        private String postalCode;
        private Double lat;
        private Double lng;
        private String country;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Bank {
        private String cardExpire;
        private String cardNumber;
        private String cardType;
        private String currency;
        private String iban;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Company {
        private String department;
        private String name;
        private String title;
        // company address flattened
        private String companyAddress;
        private String companyCity;
        private String companyState;
        private String companyStateCode;
        private String companyPostalCode;
        private String companyCountry;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Crypto {
        private String coin;
        private String wallet;
        private String network;
    }

    public UserEntity(Long id, String firstName, String lastName, String email, String ssn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ssn = ssn;
    }
}
