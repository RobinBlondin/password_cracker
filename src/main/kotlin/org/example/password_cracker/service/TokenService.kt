package org.example.password_cracker.service


import org.example.password_cracker.model.Token
import org.example.password_cracker.model.User
import org.example.password_cracker.repository.TokenRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(val tokenRepository: TokenRepository) {
    fun findToken(token: String): Optional<Token> {
        return tokenRepository.findByToken(token)
    }

    fun deleteToken(token: String) {
        tokenRepository.findByToken(token).ifPresent { tokenRepository.delete(it) }
    }

    fun save(token: Token) {
        tokenRepository.save(token)
    }
 }