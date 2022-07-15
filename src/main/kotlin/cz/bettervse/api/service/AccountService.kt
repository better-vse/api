package cz.bettervse.api.service

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import cz.bettervse.api.domain.Account
import cz.bettervse.api.domain.errors.AccountNotFoundError
import cz.bettervse.api.domain.errors.AccountVerificationError
import cz.bettervse.api.domain.errors.InvalidVerificationCodeError
import cz.bettervse.api.repository.AccountRepository
import cz.bettervse.api.security.JwtAuthenticationService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.security.SecureRandom
import kotlin.random.Random

@Service
class AccountService(
    private val repository: AccountRepository,
    private val emailService: EmailService,
    private val authenticationService: JwtAuthenticationService,
) {
    suspend fun createAccount(username: String): Account {
        val account = repository.findAccountByUsername(username).awaitSingleOrNull() ?: Account(username = username)
        val code = generateVerificationCode()

        emailService.sendVerificationEmail(account.email, code)

        return repository.save(account.copy(code = code)).awaitSingle()
    }

    private fun generateVerificationCode(): String {
        val charset = ('A'..'Z').toSet()

        // Generate a cryptographically secure random seed that can be used in PRNG
        val seed = SecureRandom.getSeed(Long.SIZE_BYTES)
        val source = Random(ByteBuffer.wrap(seed).long)

        return generateSequence { charset.random(source) }.take(8).joinToString("")
    }

    suspend fun verifyAccount(username: String, code: String): Validated<AccountVerificationError, String> {
        val account = repository.findAccountByUsername(username).awaitSingleOrNull() ?: return AccountNotFoundError.invalid()

        if (account.code != code) {
            return InvalidVerificationCodeError.invalid()
        }

        val updated = repository.save(account.copy(code = null)).awaitSingle()
        val token = authenticationService.createJwtToken(updated)

        return token.credentials.valid()
    }
}