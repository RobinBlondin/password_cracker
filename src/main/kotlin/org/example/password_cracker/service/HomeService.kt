package org.example.password_cracker.service

import lombok.Data
import org.springframework.stereotype.Service
import java.io.File
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
        val list = File("sorted_hashes.txt").readLines()

        var startIndex = 0
        var endIndex = list.size - 1

        while(startIndex <= endIndex) {
            val middleIndex = (startIndex + endIndex) / 2
            val (password, currentHash) = list[middleIndex].split(" : ")

            if(hash < currentHash) {
                endIndex = middleIndex - 1
            } else if (hash > currentHash)  {
                startIndex = middleIndex + 1
            } else {
                return "Password: $password"
            }
        }
        return "Could not find password"
    }
}