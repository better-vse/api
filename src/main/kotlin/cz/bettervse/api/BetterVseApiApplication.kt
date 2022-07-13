package cz.bettervse.api

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
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
}

fun main(args: Array<String>) {
    runApplication<BetterVseApiApplication>(*args)
}
