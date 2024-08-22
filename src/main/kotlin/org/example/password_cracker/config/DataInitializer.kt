package org.example.password_cracker.config

import org.example.password_cracker.enums.Role
import org.example.password_cracker.model.User
import org.example.password_cracker.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@Configuration
class DataInitializer {

    @Bean
    fun init(userRepository: UserRepository, passwordEncoder: PasswordEncoder) = ApplicationRunner {

        if(userRepository.findUserByEmail("admin@cracker.se").isPresent) {
            return@ApplicationRunner
        }

        val admin = User(id = UUID.randomUUID(), email = "admin@cracker.se", password = passwordEncoder.encode("password"), role = Role.ADMIN)
        val client = User(id = UUID.randomUUID(), email = "client@cracker.se", password = passwordEncoder.encode("password"), role = Role.CLIENT)
        userRepository.saveAll(listOf(admin, client))
    }
}