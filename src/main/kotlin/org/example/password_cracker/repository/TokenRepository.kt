package org.example.password_cracker.repository

import org.example.password_cracker.model.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository: JpaRepository<Token, UUID> {
   fun findByToken(token: String): Optional<Token>
}