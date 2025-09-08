package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name="uk_users_email", columnNames = "email"))
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends AbstractEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "phone", length = 20)
    private String phone;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",
                    foreignKey = @ForeignKey(name="fk_user_roles_user")))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Set<UserRole> roles;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;


    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }


public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }






public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
