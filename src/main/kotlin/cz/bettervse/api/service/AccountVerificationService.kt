package cz.bettervse.api.service

import cz.bettervse.api.configuration.JwtConfiguration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import kotlin.random.Random

@Service
class AccountVerificationService(
    private val mailer: JavaMailSender,
    private val templates: SpringTemplateEngine,
    private val configuration: JwtConfiguration
) {

    fun generateVerificationCode(): String {
        val charset = ('A'..'Z').toSet()

        // Generate a cryptographically secure random seed that can be used in PRNG
        val seed = SecureRandom.getSeed(Long.SIZE_BYTES)
        val source = Random(ByteBuffer.wrap(seed).long)

        return generateSequence { charset.random(source) }.take(8).joinToString("")
    }

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

            // TODO: Domain...
            setFrom("verification@bettervse.cz")
            setSubject("Better VŠE verification code")
            setContent(body)
        }

        mailer.send(message)
    }
}