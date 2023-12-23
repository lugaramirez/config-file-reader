package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@SpringBootApplication
class ConfigFilesApplication

fun main(args: Array<String>) {
    runApplication<ConfigFilesApplication>(*args)
}

@Component
class FileReader : ApplicationListener<ApplicationStartedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        try {
            log.info { "Application started with event: $event" }
            log.info { Files.readString(Path.of("/config/something.yml")) }
        } catch (e: Exception) {
            log.error(e) { "Could not found file. Exception was: ${e.message}" }
        }
    }
}
