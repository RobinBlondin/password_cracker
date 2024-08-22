package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import java.io.File

class WriteFile(private val homeService: HomeService): CommandLineRunner {

    override fun run(vararg args: String?) {
        val file = File("passwords.txt")
        file.writeText("")

        for ((key, value) in homeService.md5Map) {
            file.appendText("$key:$value\n")
        }

        for ((key, value) in homeService.sha256Map) {
            file.appendText("$key:$value\n")
        }
    }
}