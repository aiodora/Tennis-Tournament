package com.sd.tennis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "contact_info", length = 100)
    private String contactInfo;

    @OneToMany(mappedBy = "player1")
    private Set<Match> matchesP1 = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player2")
    private Set<Match> matchesP2 = new LinkedHashSet<>();

    @OneToMany(mappedBy = "referee")
    private Set<Match> matchesR = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player")
    private Set<Registration> registrations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "winner")
    private Set<Match> matches = new LinkedHashSet<>();

    public void setPhoneNumber(String phoneNumber) {
        this.contactInfo = phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}