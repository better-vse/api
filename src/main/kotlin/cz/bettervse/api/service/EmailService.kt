package cz.bettervse.api.service

import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

@Service
class EmailService(
    private val mailer: JavaMailSender,
    private val templates: SpringTemplateEngine,
) {
    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    @Suppress("UsePropertyAccessSyntax")
    fun sendVerificationEmail(email: String, code: String) {
        val text = MimeBodyPart()
        val html = MimeBodyPart()

        val context = Context().apply {
            setVariable("code", code)
            setVariable("email", email)
        }

        text.setContent("Your Better VŠE verification code is $code", "text/plain; charset=UTF-8")
        html.setContent(templates.process("verification-email", context), "text/html; charset=UTF-8")

        val body = MimeMultipart("alternative", text, html)
        val message = mailer.createMimeMessage().apply {
            addRecipient(Message.RecipientType.TO, InternetAddress(email))

            setFrom("better-vse@vrba.dev")
            setSubject("Better VŠE verification code")
            setContent(body)
        }

        logger.info("Sending verification code $code to $email")
        mailer.send(message)
    }
}