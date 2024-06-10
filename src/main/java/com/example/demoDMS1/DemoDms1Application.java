package com.example.demoDMS1;

import com.example.demoDMS1.Config.CephConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(CephConfig.class)
@ComponentScan
public class DemoDms1Application {

	public static void main(String[] args) {
		SpringApplication.run(DemoDms1Application.class, args);
	}

}
