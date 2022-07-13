package cz.bettervse.api.exceptions

import org.springframework.security.core.AuthenticationException

class InvalidJwtTokenException(message: String?) : AuthenticationException(message)