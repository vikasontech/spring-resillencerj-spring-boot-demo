package com.example.springresillencerjdemo


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@SpringBootApplication
class SpringResilienceDemoApplication

@RestController
class SomeController {

    @Autowired
    private lateinit var someService: SomeService

    @GetMapping("/process", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun somRequest(@RequestParam id: Int): Mono<String> {
        return someService.process(id)

    }
}

@Service
class SomeService {

    @CircuitBreaker(name = "processService", fallbackMethod = "fallbackProcess")
    fun process(id: Int): Mono<String> {
        return if (id < 1) Mono.error { IllegalArgumentException("oh!! invalid id") }
        else Mono.just("Id Found!")
    }

    fun fallbackProcess(id: Int, exp: Throwable): Mono<String> {
        log.error("eh!!! this is the error ${exp.localizedMessage}")
        return Mono.just("From fallback method: Id Not found ")
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringResilienceDemoApplication>(*args)
}
