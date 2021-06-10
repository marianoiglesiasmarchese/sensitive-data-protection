package com.sensitive.info.obfuscation

import com.sensitive.info.annotation.HideDate
import com.sensitive.info.annotation.HideEmail
import com.sensitive.info.annotation.HideNumber
import com.sensitive.info.annotation.HideText
import com.sensitive.info.annotation.Sensitive
import java.lang.reflect.Field
import java.time.LocalDate
import java.util.Date

data class DynamicClass(
//    private val attributes: MutableMap<String, Any> = LinkedHashMap(),
    private var printedClass: String = ""
) {

    companion object {
        private val CONVERTIBLE_TYPES =
            listOf(Long::class.java, String::class.java, Date::class.java, LocalDate::class.java)
        private const val CLASS_NAME_PROPERTY = "class_name_prop"
        private val SENSITIVE_ANNOTATIONS =
            listOf(HideDate::class, HideEmail::class, HideText::class, HideNumber::class)

        /**
         * we had to evaluate previously at CustomAppender level, that the log parameter had the @Sensitive annotation
         */
        fun of(obj: Any): DynamicClass {
            val dynamicClass = DynamicClass()
            dynamicClass.printedClass = printDynamicClass(obj)
            return dynamicClass
        }

        private fun printDynamicClass(obj: Any): String {
            var result = "{className}("
            // go over object fields
            obj::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                if (!CONVERTIBLE_TYPES.contains(field?.type)) {
                    // for those which not fields request map creation
                    result = of(result, field?.name!!, field.get(obj)!!)
                } else {
                    // for those which are supported field types get the field sensitive annotation
                    val sensitiveAnnotated: Annotation? =
                        field?.declaredAnnotations?.find { it.annotationClass in SENSITIVE_ANNOTATIONS }
                    // if the field has some field sensitive annotation
                    result = if (sensitiveAnnotated != null) {
                        //  add to attributes a sensitive field instead of the original
                        addSensitiveAnnotatedAttribute(result, sensitiveAnnotated, obj, field)
                    } else {
                        // if it doesn't have any field sensitive annotation, add the field to attributes
                        addNewAttributeToString(result, field?.name!!, field.get(obj)!!)
                    }
                }
            }
            // add the class name to the map tagged as CLASS_NAME_PROPERTY
            result = result.replace("{className}", obj.javaClass.simpleName)
            result = result.substring(0, result.length - 2).plus(")")
            return result
        }

        private fun of(result: String, fieldName: String, obj: Any): String {
            val pair = if (obj::class.annotations.filterIsInstance<Sensitive>().isNotEmpty()) {
                fieldName to of(obj)
            } else {
                fieldName to obj
            }
            return addNewAttributeToString(result, pair.first, pair.second)
        }

        private fun addNewAttributeToString(str: String, key: String, value: Any): String =
            str.plus("$key=$value, ")

        /**
         * chose which protected field fits to the field annotation definition
         * TODO enhance each ProtectedField configuration with annotation metadata
         */
        private fun addSensitiveAnnotatedAttribute(
            result: String,
            annotation: Annotation,   // TODO see how to group annotation under some kind of hierarchy
            obj: Any,
            field: Field
        ): String {
            val value = field.get(obj)
            return when (annotation) {
                is HideDate -> {
                    if (value is String || value is Date || value is LocalDate) // TODO try to improve this line
                        addNewAttributeToString(result, field.name, ProtectedField.ProtectedDateField(value).toString())
                    else
                        fieldProtectionNotSupported(result, field.name)
                }
                is HideEmail -> {
                    if (value is String)
                        addNewAttributeToString(
                            result,
                            field.name,
                            ProtectedField.ProtectedEmailField(value as String).toString()
                        )
                    else
                        fieldProtectionNotSupported(result, field.name)
                }
                is HideNumber -> {
                    if (value is Long)

                        addNewAttributeToString(
                            result,
                            field.name,
                            ProtectedField.ProtectedNumberField(value as Long).toString()
                        )
                    else
                        fieldProtectionNotSupported(result, field.name)
                }
                is HideText -> {
                    if (value is String)

                        addNewAttributeToString(
                            result,
                            field.name,
                            ProtectedField.ProtectedTextField(value as String).toString()
                        )
                    else
                        fieldProtectionNotSupported(result, field.name)
                }
                else -> {  // TODO this case shouldn't happen
                    fieldProtectionNotSupported(result, field.name)
                }
            }
        }

        private fun fieldProtectionNotSupported(stg: String, fieldName: String) = addNewAttributeToString(
            stg,
            fieldName,
            "Field protection not supported"
        )
    }

    override fun toString() = printedClass

    private fun getObjectInnerContent(toPrint: Map<String, Any>): String {
        var result = "{className}("
        toPrint.forEach { (key, value) ->
            if (value is Map<*, *>) {
                // the root object was an object
                if (value.keys.contains(CLASS_NAME_PROPERTY)) {
                    getObjectInnerContent(value as Map<String, Any>)
                } else { // the root object was a map
                    result = addNewAttributeToString(result, key, value)
                }
            } else {
                result = if (key == CLASS_NAME_PROPERTY) {
                    result.replace("{className}", value.toString())
                } else {
                    addNewAttributeToString(result, key, value)
                }
            }
        }
        result = result.substring(0, result.length - 2).plus(")")
        return result
    }

    private fun addNewAttributeToString(str: String, key: String, value: Any): String =
        str.plus("$key=$value, ")
}
