package com.example.demoDMS1.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="employees")
public class EmployeeEntity {

    @Id
    private String employeeId;

    private String employeeName;
    private String employeeRole;
    private String employeeEmail;
    private String employeePassword;
}
