package com.madebyher.config;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            UserRepository users,
            CategoryRepository categories,
            PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmailIgnoreCase("admin@madebyher.in").isEmpty()) {
                users.save(User.builder()
                        .fullName("Made By Her Admin")
                        .email("admin@madebyher.in")
                        .password(encoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build());
            }

            if (categories.count() == 0) {
                categories.save(Category.builder().name("Home Decor").description("Handmade decor for your home").build());
                categories.save(Category.builder().name("Fashion & Accessories").description("Bags, clothing and accessories").build());
                categories.save(Category.builder().name("Jewellery").description("Handmade jewellery").build());
                categories.save(Category.builder().name("Kitchen & Dining").description("Handmade kitchen products").build());
                categories.save(Category.builder().name("Art & Crafts").description("Traditional art and crafts").build());
                categories.save(Category.builder().name("Personal Care").description("Natural handmade personal care").build());
                categories.save(Category.builder().name("Festive & Gifts").description("Gifts and festive products").build());
            }
        };
    }
}
