package org.example.password_cracker.service

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.io.File

@Service
class HomeService {
    val sha256Map = mutableMapOf<String, String>()
    val md5Map = mutableMapOf<String, String>()

    private fun hashPassword(password: String): Pair<String, String> {
        val sha = MessageDigest.getInstance("SHA-256")
        val md = MessageDigest.getInstance("MD5")
        val shaHash = sha.digest(password.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }
        val mdHash = md.digest(password.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }

        return Pair(shaHash, mdHash)
    }

    fun saveEntryToFile(password: String): Pair<String, String> {
        val hashes = hashPassword(password)
        sha256Map[password] = hashes.first
        md5Map[password] = hashes.second

    fun saveEntryToFile(password: String): String {
        val hashedPassword = hashPassword(password)
        val entry = "$password:$hashedPassword"
        val file = File("passwords.txt")
        file.appendText(entry + "\n")

        return hashedPassword
    }
}