package cz.bettervse.api.controller

import cz.bettervse.api.domain.Account
import cz.bettervse.api.request.AccountInformationResponse
import cz.bettervse.api.request.CreateAccountRequest
import cz.bettervse.api.request.VerifyAccountRequest
import cz.bettervse.api.response.CreateAccountResponse
import cz.bettervse.api.response.VerifyAccountResponse
import cz.bettervse.api.service.AccountService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/account", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
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
                { error -> throw ResponseStatusException(error.status, error.message) },
                { response -> ResponseEntity.ok(response) }
            )
    }

    @PostMapping("/info")
    suspend fun accountInformation(@AuthenticationPrincipal account: Account): ResponseEntity<AccountInformationResponse> {
        return ResponseEntity.ok(
            AccountInformationResponse(
                account.id,
                account.username
            )
        )
    }
}