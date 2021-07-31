package com.example.sensitive.sensitivedata

import java.time.LocalDate
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

import com.google.auto.value.AutoValue


@Component
class Runner {

    private val logger = LoggerFactory.getLogger(Runner::class.java)

    @Bean
    fun init() = CommandLineRunner {
        /**
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
//        testIt(annotatedFields)
        performanceTest(annotatedFields, fields)*/
    }
/*
    private fun performanceTest(annotatedFields: AnnotatedFields, fields: Fields) {
        val elapsedTimeAnnotatedFields = testPerformanceWithCoroutines(annotatedFields)
        val elapsedTime = testPerformanceWithCoroutines(fields)
        logger.info("Sensitive data protection --annotatedFields --elapsedTime: [{} seconds]", elapsedTimeAnnotatedFields / 1000)
        logger.info("Sensitive data protection --nonAnnotatedFields --elapsedTime: [{} seconds]", elapsedTime / 1000)
    }

    private fun testIt(annotatedFields: AnnotatedFields) {
        logger.info("Sensitive data protection --annotatedFields: [{}]", annotatedFields)
        logger.trace("A TRACE Message")
        logger.debug("A DEBUG Message")
        logger.info("An INFO Message --[{}] --[{}] --$annotatedFields", "param1", "param2")
        logger.warn("A WARN Message")
        logger.error("An ERROR Message")
    }*/

    /**
     * If you wanna test it with a huge set of logs just add `-Dkotlinx.coroutines.debug` as part of the VM options
     */
    private fun testPerformanceWithCoroutines(testClass: Any): Double {
        val start = System.nanoTime()
        runBlocking {
            repeat(100000) { // launch a lot of coroutines
                launch(CoroutineName(it.toString())) {
                    logger.info("Sensitive data protection --annotatedFields: [{}]", testClass)
                }
            }
        }
        val person = AutoValue_Person("", 1, LocalDate.now(), mapOf(), listOf())
        return ((System.nanoTime().toDouble() - start) / 1000000)
    }
}


@AutoValue
@AutoValue.CopyAnnotations
abstract class Person {
    @Redacted abstract fun name(): String
    abstract fun id(): Long
    abstract fun localDate(): LocalDate
    // abstract fun heightBucket(): HeightBucket
    // abstract fun addresses(): MutableMap<String, Address>
    abstract fun addresses(): MutableMap<String, Any>
    abstract fun friends(): MutableList<Person>
}


/*@Autovalue
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
)*/
