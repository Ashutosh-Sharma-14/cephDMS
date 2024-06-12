package com.example.demoDMS1.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="user-table")
public class UserEntity {

    @Id
    private String userId;
    private String userRole;
    private String userEmail;
    private String userPassword;
}
