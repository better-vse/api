package cz.bettervse.api.service

import cz.bettervse.api.domain.Account
import cz.bettervse.api.repository.AccountRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val repository: AccountRepository,
    private val verificationService: AccountVerificationService
) {
    suspend fun createAccount(username: String): Account {
        val account = repository.findAccountByUsername(username).awaitSingleOrNull() ?: Account(username = username)
        val code = verificationService.generateVerificationCode()

        verificationService.sendVerificationEmail(account.email, code)

        return repository.save(account.copy(code = code)).awaitSingle()
    }
}