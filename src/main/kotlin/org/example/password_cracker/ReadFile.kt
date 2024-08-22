package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class ReadFile(private val homeService: HomeService): CommandLineRunner{

    override fun run(vararg args: String?) {
        println("Reading file")
        val file = File("passwords.txt").readLines()

        for (line in file) {
            val password = line.split(":")[0]
            val hash = line.split(":")[1]

            when(hash.length) {
                32 -> homeService.md5Map[password] = hash
                64 -> homeService.sha256Map[password] = hash
            }
        }
    }
}