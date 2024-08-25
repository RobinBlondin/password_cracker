package org.example.password_cracker.config

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class CustomAuthenticationFailureHandler : AuthenticationFailureHandler {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest, response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val errorMessage: String = URLEncoder.encode(exception.message, StandardCharsets.UTF_8)

        response.sendRedirect("/login?error=$errorMessage")
    }
}