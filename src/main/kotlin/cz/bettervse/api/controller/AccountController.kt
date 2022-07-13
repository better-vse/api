package cz.bettervse.api.controller

import cz.bettervse.api.request.CreateAccountRequest
import cz.bettervse.api.request.VerifyAccountRequest
import cz.bettervse.api.response.CreateAccountResponse
import cz.bettervse.api.response.VerifyAccountResponse
import cz.bettervse.api.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/account")
class AccountController(private val service: AccountService) {

    @PostMapping("/create")
    suspend fun createAccount(@Valid @RequestBody request: CreateAccountRequest): ResponseEntity<CreateAccountResponse> {
        val account = service.createAccount(request.username)
        val response = CreateAccountResponse("Verification code sent to ${account.email}")

        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify")
    suspend fun verifyAccount(@Valid @RequestBody request: VerifyAccountRequest): ResponseEntity<VerifyAccountResponse> {
        return service.verifyAccount(request.username, request.code)
            .map { token -> VerifyAccountResponse(request.username, token) }
            .fold(
                { error -> throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, error.message) },
                { response -> ResponseEntity.ok(response) }
            )
    }

}