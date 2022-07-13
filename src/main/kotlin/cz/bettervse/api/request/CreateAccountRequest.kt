package cz.bettervse.api.request

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateAccountRequest(
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[\\da-z]+$")
    val username: String
)