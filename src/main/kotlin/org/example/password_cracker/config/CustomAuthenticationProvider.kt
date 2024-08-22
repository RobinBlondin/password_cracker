package org.example.password_cracker.config

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(
    private val userDetailsService: CustomUserDetailsService,
    private val passwordEncoder: PasswordEncoder
): AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val userDetails = userDetailsService.loadUserByUsername(username)

        return if (passwordEncoder.matches(password, userDetails.password)) {
            UsernamePasswordAuthenticationToken(username, password, userDetails.authorities)
        } else {
            throw Exception("Invalid credentials")
        }
    }

    override fun supports(authentication: Class<*>?): Boolean = UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication!!)

}