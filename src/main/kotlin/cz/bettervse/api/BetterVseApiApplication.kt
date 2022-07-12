package cz.bettervse.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BetterVseApiApplication

fun main(args: Array<String>) {
    runApplication<BetterVseApiApplication>(*args)
}
