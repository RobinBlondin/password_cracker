package org.example.password_cracker

import io.github.cdimascio.dotenv.Dotenv
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PasswordCrackerApplicationTests {

	@Test
	fun contextLoads() {
	}

	companion object {
		@BeforeAll
		@JvmStatic
		fun setup() {
			val dotenv = Dotenv.load()
			dotenv.entries().forEach { entry ->
				System.setProperty(entry.key, entry.value)
			}
		}
	}

}
