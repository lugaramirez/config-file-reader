package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@ConfigurationPropertiesScan
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
    val props: Props,
) : ApplicationListener<ContextRefreshedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        configurableEnvironment.propertySources
            .filter { !it.name.contains("system") }
            .forEach {
                log.info { "===\n${it.name}: ${it.source}" }
            }
        log.info { "===\nchecking the config prop class:\n${props.another}\n${props.something}" }
    }
}

enum class Filenames(val filename: String)  {
    SOMETHING("something"),
    ANOTHER("another"),
}

@ConfigurationProperties(prefix = "blah")
data class Props(
    val another: List<String>?,
    val something: List<String>?,
)

@RestController
class RestartController {
    @PostMapping("/restart")
    fun restart() {
        ConfigFilesApplication.restart()
    }
}
