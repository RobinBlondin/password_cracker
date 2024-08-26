package org.example.password_cracker.service

import lombok.Data
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
@Data
class HomeService {
    fun hashPassword(password: String): Pair<String, String> {
        val shaHash = encode(password, "SHA-256")
        val mdHash = encode(password, "MD5")

        return Pair(shaHash, mdHash)
    }

    fun encode(password: String, algorithm: String): String {
        val md = MessageDigest.getInstance(algorithm)
        return md.digest(password.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }
    }

    fun isHexadecimal(input: String): Boolean {
        val hexRegex = Regex("^[0-9a-fA-F]+$")
        return hexRegex.matches(input)
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