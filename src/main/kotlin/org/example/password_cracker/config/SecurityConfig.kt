package org.example.password_cracker.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    @Lazy private val customAuthenticationProvider: CustomAuthenticationProvider) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Autowired
    private lateinit var authenticationFailureHandler: CustomAuthenticationFailureHandler

    @Bean
     fun configure(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { csrf ->
                csrf.disable()
            }
            .authenticationProvider(customAuthenticationProvider)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/login", "/oauth2/**", "/register").permitAll()
                    .requestMatchers("/home", "/crack").authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll()
            }
            .formLogin { formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .failureHandler(authenticationFailureHandler)
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