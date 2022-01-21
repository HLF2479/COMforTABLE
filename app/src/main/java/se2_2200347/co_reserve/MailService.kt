package se2_2200347.co_reserve

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread


class MailService(s: String, b: String) {
    private val sub = s
    private val body = b

    fun send() {
        thread {
            val property = Properties()
            property.put("mail.smtp.host", "smtp.gmail.com")
            property.put("mail.smtp.auth", "true")
            property.put("mail.smtp.starttls.enable", "true")
            property.put("mail.smtp.port", "587")
            property.put("mail.smtp.debug", "true")

            val message: Message = MimeMessage(Session.getDefaultInstance(property, object: Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("ecc22comfortable@gmail.com", "ecc1234567890")
                }
            }))
            message.setFrom(InternetAddress("ecc22comfortable@gmail.com", "COMforTABLE"))
            message.setRecipient(Message.RecipientType.TO, InternetAddress("ecccomp22347@gmail.com", "進路指導課"))
            message.subject = sub
            message.setText(body)
            Transport.send(message)
        }
    }
}