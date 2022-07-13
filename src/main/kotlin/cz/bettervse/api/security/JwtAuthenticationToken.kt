package cz.bettervse.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class JwtAuthenticationToken(private val token: String) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    override fun getCredentials(): String = token
    override fun getPrincipal(): String = token
}