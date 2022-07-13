package cz.bettervse.api.domain.errors

sealed class AccountVerificationError(val message: String)

object AccountNotFoundError : AccountVerificationError("The requested account could not be found")
object InvalidVerificationCodeError : AccountVerificationError("The provided verification code is not valid")