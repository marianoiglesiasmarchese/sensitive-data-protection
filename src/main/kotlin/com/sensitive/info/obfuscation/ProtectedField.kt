package com.sensitive.info.obfuscation

import java.time.LocalDate
import java.util.Date

sealed class ProtectedField {

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
                "***LONG_OBFUSCATION_ERROR***"
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
                "***TEXT_OBFUSCATION_ERROR***"
            }
        }

        fun hideDate(date: String): String {
            return try {
                date.replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
            } catch (ex: Exception) {
                "***STRING_DATE_OBFUSCATION_ERROR***"
            }
        }

        fun hideDate(date: Date, pattern: String = "MM/dd/yyyy"): String {
            return try {
                DateUtil.convertDateToString(date, pattern).replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
            } catch (ex: Exception) {
                "***DATE_OBFUSCATION_ERROR***"
            }
        }

        fun hideDate(date: LocalDate, pattern: String = "MM/dd/yyyy"): String {
            return try {
                DateUtil.convertDateToString(date, pattern).replace(SensitiveDataRegex.ONLY_DIGITS_REGEX.regex, "*")
            } catch (ex: Exception) {
                "***LOCAL_DATE_OBFUSCATION_ERROR***"
            }
        }

        fun hideEmail(email: String): String {
            return try {
                val matchedValues = SensitiveDataRegex.EMAIL_SENSITIVE_POSITION_GROUPS.regex.find(email)?.groupValues!!
                val publicChars = matchedValues[1]
                val hiddenChars = matchedValues[2].replace(Regex("."), "*")
                val domainName = matchedValues[3]
                publicChars + hiddenChars + domainName
            } catch (ex: Exception) {
                "***EMAIL_OBFUSCATION_ERROR***"
            }
        }
    }

    class ProtectedEmailField(private val email: String) : ProtectedField() {
        override fun toString() = hideEmail(email)
    }

    class ProtectedDateField<T>(private val date: T) : ProtectedField() {
        override fun toString() = when (date) {
            is Date -> hideDate(date)
            is String -> hideDate(date)
            is LocalDate -> hideDate(date)
            else -> "Date format not supported"
        }
    }

    class ProtectedTextField(private val text: String) : ProtectedField() {
        override fun toString() = hideText(text)
    }

    class ProtectedNumberField(private val number: Long) : ProtectedField() {
        override fun toString() = hideNumber(number)
    }
}
