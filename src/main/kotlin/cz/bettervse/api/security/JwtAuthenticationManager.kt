package cz.bettervse.api.security

import cz.bettervse.api.exceptions.InvalidJwtTokenException
import cz.bettervse.api.repository.AccountRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwt: JwtAuthenticationService,
    private val repository: AccountRepository
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
            .filter { it is JwtAuthenticationToken }
            .cast(JwtAuthenticationToken::class.java)
            .flatMap { token -> mono { validate(token) } }
            .onErrorMap { error -> InvalidJwtTokenException(error.message) }
    }

    private suspend fun validate(token: JwtAuthenticationToken): Authentication {
        val username = jwt.getUsernameFromToken(token) ?: throw InvalidJwtTokenException("The provided JWT token is not valid")
        val account = repository.findAccountByUsername(username).awaitSingleOrNull() ?: throw InvalidJwtTokenException("No matching account found")
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return UsernamePasswordAuthenticationToken(account, token.credentials, authorities)
    }

}