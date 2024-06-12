package com.example.demoDMS1.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserRoleService {
    private Map<String,Integer> userRoleMappings;

//  For bean/component creation, the constructor should do the object creation and property assignment.
//  Any other thing should be done separately. The @PostConstruct annotation helps to do this.
//  The component is created and properties are initialized and then userRoleMappings are loaded
    @PostConstruct
    public void init() {
        userRoleMappings = new HashMap<>();
        userRoleMappings.put("admin",0);
        userRoleMappings.put("zonal-admin", 1);
        userRoleMappings.put("branch-manager", 2);
        userRoleMappings.put("risk-officer", 3);
        userRoleMappings.put("branch-employee",4);
    }

    public Integer getUserAuthorityLevel(String role) {
        return userRoleMappings.get(role);
    }

    public Map<String, Integer> getAllMappings() {
        return userRoleMappings;
    }
}
