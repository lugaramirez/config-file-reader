package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path


@SpringBootApplication
class ConfigFilesApplication {
    companion object {
        @JvmStatic
        private lateinit var context: ConfigurableApplicationContext
        @JvmStatic
        fun restart() {
            Thread {
                context = context.let { previous ->
                    val args = previous.getBean(ApplicationArguments::class.java).toString()
                    previous.close()
                    runApplication<ConfigFilesApplication>(args)
                }
            }.also { thread ->
                thread.isDaemon = false
                thread.start()
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            context = runApplication<ConfigFilesApplication>(*args)
        }
    }
}

@Component
class FileReader(
    val configurableEnvironment: ConfigurableEnvironment,
) : ApplicationListener<ContextRefreshedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        try {
            log.info { "Application started with event: $event" }
            Filenames.entries.forEach {
                log.info { "\n${Files.readString(Path.of("/config/${it.filename}.yml"))}" }
            }
            configurableEnvironment.propertySources.forEach {
                log.info { "${it.name}: ${it.source}" }
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

@RestController
class RestartController {
    @PostMapping("/restart")
    fun restart() {
        ConfigFilesApplication.restart()
    }
}
