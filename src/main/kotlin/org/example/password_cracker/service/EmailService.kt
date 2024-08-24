package org.example.password_cracker.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(val javaMailSender: JavaMailSender) {
    fun sendVerificationEmail(to: String, token: String) {
        val subject = "Verification"
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(verificationEmail(token), true)

        javaMailSender.send(message)
    }

    fun verificationEmail(token: String): String {
        return """
            <h1>Welcome!</h1>
            <p>Thank you for registering with our service. Please click the link below to verify your email address:</p>
            <p><a href="http://localhost:8080/verify/$token">Verify Email</a></p>
        """.trimIndent()
    }
}