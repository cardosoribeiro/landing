package br.com.landing.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment
    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password") // Store the HASH, not the plain password
    private String password;

    @Column(name = "role")
    private String role; // e.g., "student", "instructor", "admin"

    // Getters and setters (essential!)

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}       