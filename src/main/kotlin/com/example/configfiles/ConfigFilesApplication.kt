package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@SpringBootApplication
class ConfigFilesApplication

fun main(args: Array<String>) {
    runApplication<ConfigFilesApplication>(*args)
}

@Component
class FileReader : ApplicationListener<ContextRefreshedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        try {
            log.info { "Application started with event: $event" }
            Filenames.entries.forEach {
                log.info { "\n${Files.readString(Path.of("/config/${it.filename}.yml"))}" }
            }
        } catch (e: Exception) {
            log.error(e) { "Could not found file. Exception was: ${e.message}" }
        }
    }
}

enum class Filenames(val filename: String)  {
    SOMETHING("something"),
    ANOTHER("another"),
}
