package cz.bettervse.api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: JwtAuthenticationManager,
        authenticationConverter: JwtAuthenticationConverter
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(authenticationManager).also {
            it.setServerAuthenticationConverter(authenticationConverter)
        }

        return http {
            authorizeExchange {
                authorize("/api/v1/account/*", permitAll)
                authorize("/api/v1/**", hasRole("USER"))
                authorize(anyExchange, permitAll)
            }

            addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)

            cors { }
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
        }
    }

    @Bean
    fun authenticationManager(manager: JwtAuthenticationManager): AuthenticationManager {
        // This disables the built-in authentication manager
        return AuthenticationManager { authentication -> manager.authenticate(authentication).block() }
    }

}