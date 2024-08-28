package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import java.io.File


class ParsePasswords: CommandLineRunner{
    private val homeService = HomeService()

    override fun run(vararg args: String?) {
        println("Reading file")
        File("hashes.txt")
        File("passwords.txt").useLines { lines ->
            File("hashes.txt").printWriter().use { writer ->
                lines.forEach {
                    val hash = homeService.encode(it, "SHA-256")
                    writer.println("$it : $hash")
                }
            }

        }
        println("File has been parsed")
    }
}