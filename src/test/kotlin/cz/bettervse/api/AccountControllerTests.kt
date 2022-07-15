package cz.bettervse.api

import com.ninjasquad.springmockk.MockkBean
import cz.bettervse.api.domain.Account
import cz.bettervse.api.repository.AccountRepository
import cz.bettervse.api.request.CreateAccountRequest
import cz.bettervse.api.request.VerifyAccountRequest
import cz.bettervse.api.response.VerifyAccountResponse
import cz.bettervse.api.security.JwtAuthenticationService
import cz.bettervse.api.security.JwtAuthenticationToken
import cz.bettervse.api.service.EmailService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters.fromValue

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class AccountControllerTests() {

    @Autowired
    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var emailService: EmailService

    @MockkBean
    private lateinit var accountRepository: AccountRepository

    @MockkBean
    private lateinit var jwtAuthenticationService: JwtAuthenticationService

    @Test
    fun `test creating account without username returns bad request`() {
        client.post()
            .uri("/api/v1/account/create")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `test creating account with invalid username returns bad request`() {
        fun submit(username: String): WebTestClient.ResponseSpec {
            return client.post()
                .uri("/api/v1/account/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateAccountRequest(username))
                .exchange()
        }

        submit("usernamethatistoolong").expectStatus().isBadRequest
        submit("00test").expectStatus().isBadRequest
        submit("amogus").expectStatus().isBadRequest
        submit("TEST00").expectStatus().isBadRequest
        submit("000000").expectStatus().isBadRequest
        submit("test000").expectStatus().isBadRequest
    }

    @Test
    fun `test creating account with valid username`() {
        val email = slot<String>()
        val code = slot<String>()
        val account = slot<Account>()

        coEvery { accountRepository.findAccountByUsername("vrbj04") } coAnswers { null }
        coEvery { accountRepository.save(capture(account)) } coAnswers { account.captured }

        every { emailService.sendVerificationEmail(capture(email), capture(code)) } returns Unit

        client.post()
            .uri("/api/v1/account/create")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(CreateAccountRequest("vrbj04"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals("vrbj04@vse.cz", email.captured)
        assertEquals(account.captured.code, code.captured)
    }

    @Test
    fun `test verifying account without username and code returns bad request`() {
        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `test verifying account without username returns bad request`() {
        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("""{"code": "AMOGUSXD"}"""))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `test verifying account without code returns bad request`() {
        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("""{"username": "vrbj04"}"""))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `test verifying account that does not exist returns not found`() {
        coEvery { accountRepository.findAccountByUsername("vrbj04") } coAnswers { null }

        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(VerifyAccountRequest("vrbj04", "AMOGUSXD"))
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `test verifying account with invalid code returns unprocessable entity`() {
        coEvery { accountRepository.findAccountByUsername("vrbj04") } coAnswers { Account(username = "vrbj04", code = "AMOGUSXD") }

        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(VerifyAccountRequest("vrbj04", "LMAOXDDD"))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `test verifying account with valid code returns jwt token`() {
        val account = slot<Account>()

        coEvery { accountRepository.findAccountByUsername("vrbj04") } coAnswers { Account(username = "vrbj04", code = "AMOGUSXD") }
        coEvery { accountRepository.save(capture(account)) } coAnswers { account.captured }
        every { jwtAuthenticationService.createJwtToken(any()) } answers { JwtAuthenticationToken("token.for." + firstArg<Account>().username) }

        client.post()
            .uri("/api/v1/account/verify")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(VerifyAccountRequest("vrbj04", "AMOGUSXD"))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<VerifyAccountResponse>()
            .isEqualTo(
                VerifyAccountResponse(
                    username = "vrbj04",
                    token = "token.for.vrbj04"
                )
            )

        assertNotNull(account.captured)
        assertEquals("vrbj04", account.captured.username)
        assertNull(account.captured.code)
    }
}