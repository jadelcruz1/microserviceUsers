package com.jupyter.user_service;


import com.jupyter.user_service.Entity.User;
import com.jupyter.user_service.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner loadUsers(UserRepository repository) {
        return args -> {
            repository.save(new User(null, "Jardel", "jardel@email.com"));
            repository.save(new User(null, "Maria", "maria@email.com"));
            repository.save(new User(null, "Carlos", "carlos@email.com"));
            repository.save(new User(null, "Ana", "ana@email.com"));
            repository.save(new User(null, "Pedro", "pedro@email.com"));
        };
    }

}
