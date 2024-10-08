package org.example.password_cracker

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PasswordCrackerApplication

fun main(args: Array<String>) {
	val dotenv = Dotenv.load()
	dotenv.entries().forEach { entry ->
		System.setProperty(entry.key, entry.value)
	}

	if(args.isEmpty()) {
		runApplication<PasswordCrackerApplication>(*args)
	} else if(args[0] == "hashPasswords") {
		val application = SpringApplication(HashPasswords::class.java)
		application.webApplicationType = WebApplicationType.NONE
		application.run(*args)
	} else if(args[0] == "sortHashes") {
		val application = SpringApplication(SortHashes::class.java)
		application.webApplicationType = WebApplicationType.NONE
		application.run(*args)
	}

}
