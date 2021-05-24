package com.sensitive.info

import com.sensitive.info.utils.HideDate
import com.sensitive.info.utils.HideEmail
import com.sensitive.info.utils.HideNumber
import com.sensitive.info.utils.HideText
import com.sensitive.info.utils.Sensitive
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Runner {

    private val logger = LoggerFactory.getLogger(Runner::class.java)

    @Bean
    fun init() = CommandLineRunner {
        val annotatedFields = AnnotatedFields(
            stringDate = "22/03/1990",
            date = LocalDate.now(),
            dateWithPattern = LocalDate.now(),
            email = "mariano@test.com",
            text = "some long text",
            textWithMoreVisibility = "some long text",
            number = 1023812094710923L,
            numberWithMoreVisibility = 1023812094710923L
        )
        logger.info("Sensitive data protection --annotatedFields: {}", annotatedFields)
        logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message --[{}] --[{}] --$annotatedFields", "param1", "param2");
        logger.warn("A WARN Message");
        logger.error("An ERROR Message");
    }
}

@Sensitive
data class AnnotatedFields(
    @field: HideDate val stringDate: String,
    @field: HideDate val date: LocalDate,
    @field: HideDate("dd/MM/yyyy") val dateWithPattern: LocalDate,
    @field: HideEmail
    val email: String,
    @field: HideText val text: String,
    @field: HideText(3) val textWithMoreVisibility: String,
    @field: HideNumber val number: Long,
    @field: HideNumber(3) val numberWithMoreVisibility: Long
)
