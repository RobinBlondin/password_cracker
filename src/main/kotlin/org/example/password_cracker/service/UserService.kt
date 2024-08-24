package org.example.password_cracker.service

import org.example.password_cracker.dto.registerDTO
import org.example.password_cracker.enums.Role
import org.example.password_cracker.model.Token
import org.example.password_cracker.model.User
import org.example.password_cracker.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(val userRepository: UserRepository, val tokenService: TokenService, val passwordEncoder: PasswordEncoder) {
    fun register(dto: registerDTO): String? {
        val user = dtoTOUser(dto)
        val token = Token(user = user)

        save(user)
        tokenService.save(token)
        return token.token
    }
    fun save(user: User) {
        userRepository.save(user)
    }

    fun deleteUser(user: User) {
        userRepository.delete(user)
    }

    fun dtoTOUser(dto: registerDTO): User {
        return User(
            email = dto.email,
            password = passwordEncoder.encode(dto.password),
            role = Role.CLIENT
        )
    }

    fun findUserByEmail(email: String): Optional<User> {
        return userRepository.findUserByEmail(email)
    }

}