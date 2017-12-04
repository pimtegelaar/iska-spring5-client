package com.iska.jvmconclient

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import java.util.*

@SpringBootApplication
class JvmconclientApplication {

    @Bean
    fun client() = WebClient.create("http://localhost:8080/talks")

    @Bean
    fun demo(client: WebClient) = CommandLineRunner {
        var ratings = 0
        var total = 0
        client.get().retrieve().bodyToFlux<Talk>()
                .filter { it.title.equals(other = "The Fallacies of Doom", ignoreCase = true) }
                .flatMap {
                    client.get().uri("/{id}/ratings", it.id)
                            .retrieve().bodyToFlux<Rating>()
                }
                .subscribe {
                    println(it)
                    ratings++
                    total+= it.value!!
                    println("Average rating " + total / ratings)
                }
    }
}


fun main(args: Array<String>) {
    runApplication<JvmconclientApplication>(*args)
}

data class Rating(val id: String? = null, val value: Int? = null, val date: Date? = null)

data class Talk(val id: String? = null, val title: String? = null)