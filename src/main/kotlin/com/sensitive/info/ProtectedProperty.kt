package com.sensitive.info

import java.util.*
import org.slf4j.LoggerFactory

sealed class ProtectedProperty {

    enum class SensitiveDataRegex(val regex: Regex) {
        // Capture Group 1: first 4 chars - public email chars, it should be public on logs
        // Capture Group 2: all chars until @ - to be hidden and replaced with *
        // Capture Group 3: rest of the string - to remain as it is, it should be public on logs
        EMAIL_SENSITIVE_POSITION_GROUPS(Regex("(^.{4})(.*)(?=@)(.*)")),

        // Capture only digits, to exclude date separator, used to be replaced with * on date and to hide number fields
        ONLY_DIGITS_REGEX(Regex("\\d")),

        // Capture any word character. Used to be replaced with * on text fields
        ONLY_CHARACTERS_REGEX(Regex("\\w"))
    }

    companion object {

        private val logger = LoggerFactory.getLogger(ProtectedProperty::class.java)
        private val CLASS = ProtectedProperty::class.simpleName

        /**
         * This implementation supports numbers longer than 7 digits
         */
        fun hideNumber(number: Long): String {
            val showFirstAndLastChars = 3
            return try {
                val numberStr = number.toString()
                val hiddenChars =
                    numberStr.substring(showFirstAndLastChars, number.toString().length - showFirstAndLastChars)
                        .replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
                numberStr.replaceRange(
                    showFirstAndLastChars,
                    number.toString().length - showFirstAndLastChars,
                    hiddenChars
                )
            } catch (ex: Exception) {
                logger.error("--$CLASS:hideNumber --Exception --cause[{}]", ex.cause)
                logger.error("--$CLASS:hideNumber --Exception --message[{}]", ex.message)
                "***OBFUSCATION_ERROR***"
            }
        }

        /**
         * This implementation supports strings longer than 5 digits
         */
        fun hideText(text: String): String {
            val showFirstAndLastChars = 2
            return try {
                val hiddenChars = text.substring(showFirstAndLastChars, text.length - showFirstAndLastChars)
                    .replace(SensitiveDataRegex.ONLY_CHARACTERS_REGEX.regex, "*")
                text.replaceRange(showFirstAndLastChars, text.length - showFirstAndLastChars, hiddenChars)
            } catch (ex: Exception) {
                logger.error("--$CLASS:hideText --Exception --cause[{}]", ex.cause)
                logger.error("--$CLASS:hideText --Exception --message[{}]", ex.message)
                "***OBFUSCATION_ERROR***"
            }
        }

        fun hideDate(date: String): String {
            return try {
                date.replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
            } catch (ex: Exception) {
                logger.error("--$CLASS:hideDate --Exception --cause[{}]", ex.cause)
                logger.error("--$CLASS:hideDate --Exception --message[{}]", ex.message)
                "***OBFUSCATION_ERROR***"
            }
        }

        fun hideDate(date: Date, pattern: String = "MM/dd/yyyy"): String {
            return try {
                DateUtil.convertDateToString(date, pattern).replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
            } catch (ex: Exception) {
                logger.error("--$CLASS:hideDate --Exception --cause[{}]", ex.cause)
                logger.error("--$CLASS:hideDate --Exception --message[{}]", ex.message)
                "***OBFUSCATION_ERROR***"
            }
        }

        fun hideEmail(email: String): String {
            return SensitiveDataRegex.EMAIL_SENSITIVE_POSITION_GROUPS.regex.find(email)?.let {
                val matchedValues = it.groupValues
                try {
                    val publicChars = matchedValues[1]
                    val hiddenChars = matchedValues[2].replace(Regex("."), "*")
                    val domainName = matchedValues[3]
                    publicChars + hiddenChars + domainName
                } catch (ex: Exception) {
                    logger.error("--$CLASS:hideEmail --Exception --cause[{}]", ex.cause)
                    logger.error("--$CLASS:hideEmail --Exception --message[{}]", ex.message)
                    "***OBFUSCATION_ERROR***"
                }
            }.orEmpty()
        }
    }

    class ProtectedEmailProperty(private val email: String) : ProtectedProperty() {
        override fun toString() = hideEmail(email)
    }

    class ProtectedDateProperty<T>(private val date: T) : ProtectedProperty() {
        override fun toString() = when (date) {
            is Date -> hideDate(date)
            is String -> hideDate(date)
            else -> "Date format not supported"
        }
    }

    class ProtectedTextProperty(private val text: String) : ProtectedProperty() {
        override fun toString() = hideText(text)
    }

    class ProtectedNumberProperty(private val number: Long) : ProtectedProperty() {
        override fun toString() = hideNumber(number)
    }
}
