package com.example.demoDMS1.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/demo-controller")
public class DemoController {
    @GetMapping("/hello")
    public String sayHello(){
        System.out.println("hello world");
        return "hello from secured endpoint";
    }
}
