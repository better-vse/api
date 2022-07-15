package cz.bettervse.api

import cz.bettervse.api.configuration.JwtConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(JwtConfiguration::class)
class BetterVseApiApplication {
    @Bean
    fun connectionFactory(datasource: DataSourceProperties): ConnectionFactory {
        val url = datasource.url.replaceFirst("jdbc:", "r2dbc:")
        val options = ConnectionFactoryOptions.builder()
            .from(ConnectionFactoryOptions.parse(url))
            .option(ConnectionFactoryOptions.USER, datasource.username)
            .option(ConnectionFactoryOptions.PASSWORD, datasource.password)
            .build()

        return ConnectionFactories.get(options)
    }

    @Bean
    fun applicationRunner(@Value("\${server.port}") port: String): ApplicationRunner {
        return ApplicationRunner {
            println("Running on port $port")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<BetterVseApiApplication>(*args)
}
