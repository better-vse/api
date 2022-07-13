package cz.bettervse.api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import cz.bettervse.api.configuration.JwtConfiguration
import cz.bettervse.api.domain.Account
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class JwtAuthenticationService(private val configuration: JwtConfiguration) {

    private val algorithm = Algorithm.HMAC512(configuration.secret)

    private val verifier = JWT.require(algorithm).withIssuer(configuration.issuer).build()

    fun createJwtToken(account: Account): JwtAuthenticationToken {
        val issued = Instant.now()
        val expiration = issued + Duration.ofDays(365 * 2)
        val token = JWT.create()
            .withIssuer(configuration.issuer)
            .withSubject(account.username)
            .withIssuedAt(issued)
            .withExpiresAt(expiration)
            .sign(algorithm)

        return JwtAuthenticationToken(token)
    }

    fun getUsernameFromToken(token: JwtAuthenticationToken): String? {
        return try {
            val parsed = verifier.verify(token.principal)
            val username = parsed.subject

            username
        }
        catch (_: JWTVerificationException) {
            null
        }
    }

}