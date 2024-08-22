package org.example.password_cracker.service

import lombok.Data
import org.example.password_cracker.WriteFile
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
@Data
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

        WriteFile(this).run()
        return hashes
    }

    fun crackHash(hash: String): String {
        val result = if(hash.length == 64) {
            sha256Map.filterValues { it == hash }.keys.firstOrNull()
        } else {
            md5Map.filterValues { it == hash }.keys.firstOrNull()
        }

        return when(result) {
            null -> "Password not found"
            else -> "Password: $result"
        }
    }
}