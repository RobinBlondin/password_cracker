package org.example.password_cracker.service

import lombok.Data
import org.springframework.stereotype.Service
import java.io.RandomAccessFile
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
        val path = when {
            isHexadecimal(hash) && hash.length == 64 -> "files/sorted_sha256.txt"
            isHexadecimal(hash) && hash.length == 32 -> "files/sorted_md5.txt"
            else -> throw IllegalArgumentException("Invalid hash")
        }

        RandomAccessFile(path, "r").use { file ->
            var startIndex = 0L
            var endIndex = file.length() - 1

            while(startIndex <= endIndex) {
                val middleIndex = (startIndex + endIndex) / 2
                file.seek(middleIndex)

                if(middleIndex != 0L) {
                    file.readLine()
                }

                val line = file.readLine()

                if(line != null) {
                    val currentHash = line.split(" : ")[1]
                    when {
                        hash < currentHash -> endIndex = middleIndex - 1
                        hash > currentHash -> startIndex = middleIndex + 1
                        else -> return "Password: ${line.split(" : ")[0]}"
                    }
                } else {
                    break
                }
            }
        }

        return "Could not find password"
    }
}