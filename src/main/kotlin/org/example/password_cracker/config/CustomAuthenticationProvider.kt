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

        return if (passwordEncoder.matches(password, userDetails.password)) {
            UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)
        } else {
            throw BadCredentialsException("Invalid credentials")
        }
    }

    override fun supports(authentication: Class<*>?): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication!!)


}