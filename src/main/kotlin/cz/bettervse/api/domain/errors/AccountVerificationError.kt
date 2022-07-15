package cz.bettervse.api.domain.errors

import org.springframework.http.HttpStatus

sealed class AccountVerificationError(val status: HttpStatus, val message: String)

object AccountNotFoundError : AccountVerificationError(HttpStatus.NOT_FOUND, "The requested account could not be found")
object InvalidVerificationCodeError : AccountVerificationError(HttpStatus.UNPROCESSABLE_ENTITY, "The provided verification code is not valid")