package cz.bettervse.api.repository

import cz.bettervse.api.domain.Account
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountRepository : ReactiveCrudRepository<Account, Int> {

    fun findAccountByUsername(username: String): Mono<Account>

    fun findAccountByUsernameAndCode(username: String, code: String): Mono<Account>

}