package cz.bettervse.api.request

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateAccountRequest(
    @field:Size(min = 6, max = 6)
    @field:Pattern(regexp = "^[a-z]+\\d+$")
    val username: String
)