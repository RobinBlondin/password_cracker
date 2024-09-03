package org.example.password_cracker.service

import org.example.password_cracker.dto.RegisterDTO
import org.example.password_cracker.model.Token
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Service
class AuthService(val tokenService: TokenService, val userService: UserService, val emailService: EmailService) {
    fun registerProcess(rda: RedirectAttributes, dto: RegisterDTO) {
        val token = createUserAndToken(dto)
        val email = dto.email ?: throw Exception("Email not found")
        emailService.sendVerificationEmail(email, token)
    }

    fun createUserAndToken(dto: RegisterDTO): String {
        val user = userService.dtoTOUser(dto)
        val token = Token(user = user)

        userService.save(user)
        tokenService.save(token)

        val tokenName = token.token ?: throw Exception("Token not found")
        return tokenName
    }

    fun verificationProcess(tokenId: String, existingToken: Token) {
        val user = existingToken.user ?: throw Exception("User not found")
        user.enabled = true
        userService.save(user)
        tokenService.deleteToken(tokenId)
    }
}