package org.example.password_cracker.config

import org.example.password_cracker.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findUserByEmail(username).orElseThrow { UsernameNotFoundException("Invalid credentials") }
}