package org.example.password_cracker.repository

import org.example.password_cracker.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByEmail(email: String): Optional<User>
}