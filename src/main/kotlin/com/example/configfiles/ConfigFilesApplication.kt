package com.example.configfiles

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent
import org.springframework.cloud.kubernetes.client.config.KubernetesClientConfigMapPropertySource
import org.springframework.context.ApplicationListener
import org.springframework.core.env.CompositePropertySource
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@ConfigurationPropertiesScan
class ConfigFilesApplication

fun main(args: Array<String>) {
    runApplication<ConfigFilesApplication>(*args)
}

@Component
class FileReader(
    val configurableEnvironment: ConfigurableEnvironment,
    val props: Props,
) : ApplicationListener<RefreshScopeRefreshedEvent> {
    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: RefreshScopeRefreshedEvent) {
        configurableEnvironment.propertySources
            .filterIsInstance<CompositePropertySource>()
            .forEach { compositeSource ->
                compositeSource.propertySources.filterIsInstance<KubernetesClientConfigMapPropertySource>()
                    .forEach { configMap ->
                        log.debug { configMap.source }
                    }
            }
        log.debug {
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

@RestController
class Pong(private val props: Props) {
    @GetMapping("/ping")
    fun pong() = ResponseEntity.ok(props)
}