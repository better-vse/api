package cz.bettervse.api.repository

import cz.bettervse.api.domain.Account
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CoroutineCrudRepository<Account, Int> {

    suspend fun findAccountByUsername(username: String): Account?

}