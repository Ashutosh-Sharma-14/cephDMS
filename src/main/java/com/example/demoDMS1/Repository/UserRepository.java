package com.example.demoDMS1.Repository;

import com.example.demoDMS1.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    boolean existsByEmployeeEmail(@Param("employeeEmail") String employeeEmail);

    @Query("SELECT e.employeePassword FROM EmployeeEntity e WHERE e.employeeEmail = :employeeEmail")
    String findEncodedPasswordByEmail(@Param("employeeEmail") String employeeEmail);

    @Query("SELECT e.employeeRole FROM EmployeeEntity e WHERE e.employeeEmail = :employeeEmail")
    String findEmployeeRoleByEmployeeEmail(@Param("employeeEmail") String employeeEmail);
}
