package com.leverx.RatingSystemRest;

import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication

public class RatingSystemRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatingSystemRestApplication.class, args);

	}




}
