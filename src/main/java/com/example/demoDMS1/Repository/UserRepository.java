package com.example.demoDMS1.Repository;

import com.example.demoDMS1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByUserEmail(@Param("userEmail") String userEmail);
//    boolean existsByUserEmail(@Param("userEmail") String userEmail);
//
//    @Query("SELECT u.userPassword FROM UserEntity u WHERE u.userEmail = :userEmail")
//    String findEncodedPasswordByEmail(@Param("userEmail") String userEmail);
//
//    @Query("SELECT u.userRole FROM UserEntity u WHERE u.userEmail = :userEmail")
//    String findUserRoleByUserEmail(@Param("userEmail") String userEmail);
//
//    @Query("SELECT u.userAuthorityLevel FROM UserEntity u WHERE u.userEmail = :userEmail")
//    String findUserAuthorityLevelByUserEmail(@Param("userEmail") String userEmail);

}
