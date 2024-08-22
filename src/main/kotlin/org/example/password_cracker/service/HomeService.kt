package org.example.password_cracker.service

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.io.File

@Service
class HomeService {
    private fun hashPassword(password: String): String = BCryptPasswordEncoder().encode(password)

    fun saveEntryToFile(password: String): String {
        val hashedPassword = hashPassword(password)
        val entry = "$password:$hashedPassword"
        val file = File("passwords.txt")
        file.appendText(entry + "\n")

        return hashedPassword
    }
}