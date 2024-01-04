package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component


@SpringBootApplication
@ConfigurationPropertiesScan
class ConfigFilesApplication

fun main(args: Array<String>) {
    runApplication<ConfigFilesApplication>(*args)
}

@Component
class FileReader(val props: Props) : ApplicationListener<ContextRefreshedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        log.info {
            """Reading changed props:
            ${props.another}
            ${props.something}
            """.trimIndent()
        }
    }
}

@ConfigurationProperties(prefix = "blah")
data class Props(
    val another: List<String>?,
    val something: List<String>?,
)
