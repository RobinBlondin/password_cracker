package org.example.password_cracker

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class PasswordCrackerApplication

fun main(args: Array<String>) {
	val dotenv = Dotenv.load()  // Load the environment variables from .env file
	dotenv.entries().forEach { entry ->
		System.setProperty(entry.key, entry.value)  // Set each .env entry as a system property
	}
	runApplication<PasswordCrackerApplication>(*args)
}
