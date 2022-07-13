package cz.bettervse.api.request

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class VerifyAccountRequest(
    @field:Size(min = 6, max = 6)
    @field:Pattern(regexp = "^[a-z]+\\d+$")
    val username: String,

    @field:Size(min = 8, max = 8)
    @field:Pattern(regexp = "^[A-Z]+$")
    val code: String
)