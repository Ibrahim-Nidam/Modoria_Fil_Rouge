package com.modoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ModoriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModoriaApplication.class, args);
    }

}
