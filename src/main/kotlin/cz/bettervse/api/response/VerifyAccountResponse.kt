package cz.bettervse.api.response

data class VerifyAccountResponse(
    val username: String,
    val token: String
)