package com.hms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String specialization;

    @NotBlank
    @Column(unique = true, nullable = false, length = 15)
    private String phone;

    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    private boolean availability = true;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
