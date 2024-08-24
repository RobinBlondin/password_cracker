package org.example.password_cracker.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val properties = Properties()
        val dotenv = Dotenv.configure().load()
        val mailSender = JavaMailSenderImpl()

        properties["mail.smtp.auth"] = dotenv["MAIL_SMTP_AUTH"]
        properties["mail.smtp.starttls.enable"] = dotenv["MAIL_SMTP_STARTTLS_ENABLE"]

        mailSender.javaMailProperties = properties
        mailSender.host = dotenv["MAIL_HOST"]
        mailSender.port = dotenv["MAIL_PORT"]?.toIntOrNull() ?: 587
        mailSender.username = dotenv["MAIL_USERNAME"]
        mailSender.password = dotenv["MAIL_PASSWORD"]

        return mailSender
    }
}
