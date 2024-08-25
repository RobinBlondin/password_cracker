package org.example.password_cracker.config

import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(
    @Lazy private val userDetailsService: CustomUserDetailsService,
    @Lazy private val passwordEncoder: PasswordEncoder
): AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {

        val username = authentication.name
        val password = authentication.credentials.toString()
        val userDetails = userDetailsService.loadUserByUsername(username)

        if (!userDetails.isEnabled) {
            println("User $username is not enabled.")
            throw BadCredentialsException("Your account needs to be verified. Please check your email")
        }

        if (passwordEncoder.matches(password, userDetails.password)) {
            println("User $username successfully authenticated.")
            return UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)
        } else {
            println("Password mismatch for user $username.")
            throw BadCredentialsException("Invalid credentials")
        }
    }

    override fun supports(authentication: Class<*>?): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication!!)


}