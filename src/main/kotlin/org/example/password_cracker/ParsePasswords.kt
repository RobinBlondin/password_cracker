package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File


class ParsePasswords(): CommandLineRunner{
    val homeService = HomeService()

    override fun run(vararg args: String?) {
        println("Reading file")
        val result = File("hashes.txt")
        File("passwords.txt").readLines().forEach {
                val hash = homeService.hashPassword(it, "SHA-256")
                result.appendText("$it : $hash\n")
            }
        println("File has been parsed")
        }
}