package org.example.password_cracker.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig {
    @Value("\${spring.mail.host}")
    private lateinit var mailHost: String

    @Value("\${spring.mail.port}")
    private lateinit var mailPort: String

    @Value("\${spring.mail.username}")
    private lateinit var mailUsername: String

    @Value("\${spring.mail.password}")
    private lateinit var mailPassword: String

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private lateinit var mailSmtpAuth: String

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private lateinit var mailSmtpStarttls: String

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        val properties = Properties()

        properties["mail.smtp.auth"] = mailSmtpAuth
        properties["mail.smtp.starttls.enable"] = mailSmtpStarttls

        mailSender.javaMailProperties = properties
        mailSender.host = mailHost
        mailSender.port = mailPort.toIntOrNull() ?: 587
        mailSender.username = mailUsername
        mailSender.password = mailPassword

        return mailSender
    }
}
