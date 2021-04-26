package com.sensitive.info

import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Runner {

    private val logger = LoggerFactory.getLogger(Runner::class.java)

    @Bean
    fun init() = CommandLineRunner {
        val fields = Fields(
            stringDate = ProtectedProperty.ProtectedDateProperty("22/03/1990"),
            date = ProtectedProperty.ProtectedDateProperty(Date()),
            email = ProtectedProperty.ProtectedEmailProperty("mariano@test.com"),
            text = ProtectedProperty.ProtectedTextProperty("some long text")
        )
        logger.info("Sensitive data protection: {}", fields)
    }
}

data class Fields(
    val stringDate: ProtectedProperty.ProtectedDateProperty<String>,
    val date: ProtectedProperty.ProtectedDateProperty<Date>,
    val email: ProtectedProperty.ProtectedEmailProperty,
    val text: ProtectedProperty.ProtectedTextProperty,
    val number: ProtectedProperty.ProtectedNumberProperty? = ProtectedProperty.ProtectedNumberProperty(102301857240L)
)