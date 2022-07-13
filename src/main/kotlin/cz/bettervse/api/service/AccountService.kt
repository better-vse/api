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

@Service
class AccountService(
    private val repository: AccountRepository,
    private val verificationService: AccountVerificationService,
    private val authenticationService: JwtAuthenticationService
) {
    suspend fun createAccount(username: String): Account {
        val account = repository.findAccountByUsername(username).awaitSingleOrNull() ?: Account(username = username)
        val code = verificationService.generateVerificationCode()

        verificationService.sendVerificationEmail(account.email, code)

        return repository.save(account.copy(code = code)).awaitSingle()
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