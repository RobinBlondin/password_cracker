package org.example.password_cracker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val customAuthenticationProvider: CustomAuthenticationProvider) {

    @Bean
     fun configure(http: HttpSecurity): SecurityFilterChain =
        http
            .authenticationProvider(customAuthenticationProvider)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/home", "/crack").hasAnyRole("USER", "ADMIN")
                    .anyRequest().permitAll()
            }
            .formLogin { formLogin ->
                formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/home", true)
                    .failureUrl("/?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout.permitAll().logoutSuccessUrl("/")
            }
            .build()


    @Bean
    fun userDetailsService(): UserDetailsService = customUserDetailsService

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager


}