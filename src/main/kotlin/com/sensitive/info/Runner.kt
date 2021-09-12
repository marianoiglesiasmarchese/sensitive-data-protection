package com.sensitive.info

import com.sensitive.info.annotation.HideDate
import com.sensitive.info.annotation.HideEmail
import com.sensitive.info.annotation.HideNumber
import com.sensitive.info.annotation.HideText
import com.sensitive.info.annotation.Sensitive
import com.sensitive.info.obfuscation.ProtectedField
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.system.measureNanoTime

@Component
class Runner {

    companion object {
        const val NUM_OF_COROUTINES = 100000
    }

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
            numberWithMoreVisibility = 1023812094710923L,
            annotatedInnerClass = AnnotatedInnerClass(
                someText = "some text within inner class"
            ),
            notAnnotatedInnerClass = NotAnnotatedInnerClass(
                text = "not annotates inner class text",
                date = LocalDate.now(),
                double = 30.0
            )
        )
        val fields = Fields(
            stringDate = "22/03/1990",
            date = LocalDate.now(),
            dateWithPattern = LocalDate.now(),
            email = "mariano@test.com",
            text = "some long text",
            textWithMoreVisibility = "some long text",
            number = 1023812094710923L,
            numberWithMoreVisibility = 1023812094710923L,
            annotatedInnerClass = InnerClass(
                someText = "some text within inner class"
            ),
            notAnnotatedInnerClass = InnerClass2(
                text = "not annotates inner class text",
                date = LocalDate.now(),
                double = 30.0
            )
        )
        val overriddenFields = OverriddenFields(
            stringDate = "22/03/1990",
            date = LocalDate.now(),
            dateWithPattern = LocalDate.now(),
            email = "mariano@test.com",
            text = "some long text",
            textWithMoreVisibility = "some long text",
            number = 1023812094710923L,
            numberWithMoreVisibility = 1023812094710923L,
            annotatedInnerClass = OverriddenInnerClass(
                someText = "some text within inner class"
            ),
            notAnnotatedInnerClass = OverriddenInnerClass2(
                text = "not annotates inner class text",
                date = LocalDate.now(),
                double = 30.0
            )
        )
//        performanceTest(annotatedFields, fields, overriddenFields)
    }

    private fun performanceTest(annotatedFields: AnnotatedFields, fields: Fields, overriddenFields: OverriddenFields) {
        val warmingTime = testPerformanceWithCoroutines("warmingTime", "pre warming")
        val elapsedTime = testPerformanceWithCoroutines("nonAnnotatedFields", fields)
        val elapsedTimeForOverriddenFields = testPerformanceWithCoroutines("overriddenFields", overriddenFields)
        val elapsedTimeAnnotatedFields = testPerformanceWithCoroutines("annotatedFields", annotatedFields)
        logger.info("Sensitive data protection --warmingTime --elapsedTime: [{} seconds]", warmingTime / 1000)
        logger.info("Sensitive data protection --nonAnnotatedFields --elapsedTime: [{} seconds]", elapsedTime / 1000)
        logger.info(
            "Sensitive data protection --overriddenFields --elapsedTime: [{} seconds]",
            elapsedTimeForOverriddenFields / 1000
        )
        logger.info(
            "Sensitive data protection --annotatedFields --elapsedTime: [{} seconds]",
            elapsedTimeAnnotatedFields / 1000
        )
    }

    /**
     * If you wanna test it with a huge set of logs just add `-Dkotlinx.coroutines.debug` as part of the VM options
     */
    private fun testPerformanceWithCoroutines(logMessage: String, testClass: Any): Double {
        return measureNanoTime {
            runBlocking {
                repeat(NUM_OF_COROUTINES) { // launch a lot of coroutines
                    launch(CoroutineName(it.toString())) {
                        logger.info("Sensitive data protection --$logMessage: [{}]", testClass)
                    }
                }
            }
        }.toDouble() / 1000000
    }
}

@Sensitive
data class AnnotatedFields(
    @field: HideDate val stringDate: String,
    @field: HideDate val date: LocalDate,
    @field: HideDate("dd/MM/yyyy") val dateWithPattern: LocalDate,
    @field: HideEmail val email: String,
    @field: HideText val text: String,
    @field: HideText(3) val textWithMoreVisibility: String,
    @field: HideNumber val number: Long,
    @field: HideNumber(3) val numberWithMoreVisibility: Long,
    val annotatedInnerClass: AnnotatedInnerClass,
    val notAnnotatedInnerClass: NotAnnotatedInnerClass
)

@Sensitive
data class AnnotatedInnerClass(
    @field: HideText val someText: String,
    val map: Map<String, String> = emptyMap(),
    val list: List<String> = emptyList()
)

data class NotAnnotatedInnerClass(
    val text: String,
    val date: LocalDate,
    val double: Double
)

data class Fields(
    val stringDate: String,
    val date: LocalDate,
    val dateWithPattern: LocalDate,
    val email: String,
    val text: String,
    val textWithMoreVisibility: String,
    val number: Long,
    val numberWithMoreVisibility: Long,
    val annotatedInnerClass: InnerClass,
    val notAnnotatedInnerClass: InnerClass2
)

data class InnerClass(
    val someText: String,
    val map: Map<String, String> = emptyMap(),
    val list: List<String> = emptyList()
)

data class InnerClass2(
    val text: String,
    val date: LocalDate,
    val double: Double
)

data class OverriddenFields(
    val stringDate: String,
    val date: LocalDate,
    val dateWithPattern: LocalDate,
    val email: String,
    val text: String,
    val textWithMoreVisibility: String,
    val number: Long,
    val numberWithMoreVisibility: Long,
    val annotatedInnerClass: OverriddenInnerClass,
    val notAnnotatedInnerClass: OverriddenInnerClass2
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(" +
            "stringDate=${ProtectedField.hideDate(stringDate)}, " +
            "date=${ProtectedField.hideDate(date)}, " +
            "dateWithPattern=${ProtectedField.hideDate(dateWithPattern)}, " +
            "email=${ProtectedField.hideEmail(email)}, " +
            "text=${ProtectedField.hideText(text)}, " +
            "textWithMoreVisibility=${ProtectedField.hideText(textWithMoreVisibility)}, " +
            "number=${ProtectedField.hideNumber(number)}, " +
            "numberWithMoreVisibility=${ProtectedField.hideNumber(numberWithMoreVisibility)}, " +
            "annotatedInnerClass=$annotatedInnerClass, " +
            "notAnnotatedInnerClass=$notAnnotatedInnerClass" +
            ")"
    }
}

data class OverriddenInnerClass(
    val someText: String,
    val map: Map<String, String> = emptyMap(),
    val list: List<String> = emptyList()
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(" +
            "someText=${ProtectedField.hideText(someText)}, " +
            "map=$map, " +
            "list=$list" +
            ")"
    }
}

data class OverriddenInnerClass2(
    val text: String,
    val date: LocalDate,
    val double: Double
)
