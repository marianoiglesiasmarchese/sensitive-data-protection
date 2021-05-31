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
    private val attributes: MutableMap<String, Any> = LinkedHashMap()
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
            // go over object fields
            obj::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                if (!CONVERTIBLE_TYPES.contains(field?.type)) {
                    // for those which not fields request map creation
                    of(dynamicClass.attributes, field?.name!!, field.get(obj)!!)
                } else {
                    // for those which are supported field types get the field sensitive annotation
                    val sensitiveAnnotated: Annotation? =
                        field?.declaredAnnotations?.find { it.annotationClass in SENSITIVE_ANNOTATIONS }
                    // if the field has some field sensitive annotation
                    sensitiveAnnotated?.also { annotation ->
                        //  add to attributes a sensitive field instead of the original
                        addSensitiveAnnotatedAttribute(annotation, obj, field, dynamicClass.attributes)
                    } ?: dynamicClass.attributes.put(
                        field?.name!!,
                        field.get(obj)!!
                    ) // if it doesn't have any field sensitive annotation, add the field to attributes
                }
            }
            // add the class name to the map tagged as CLASS_NAME_PROPERTY
            dynamicClass.attributes[CLASS_NAME_PROPERTY] = obj.javaClass.simpleName
            return dynamicClass
        }

        private fun of(attributes: MutableMap<String, Any>, fieldName: String, obj: Any) {
            val pair = if (obj::class.annotations.filterIsInstance<Sensitive>().isNotEmpty()) {
                fieldName to of(obj)
            } else {
                fieldName to obj
            }
            attributes[pair.first] = pair.second
        }

        /**
         * chose which protected field fits to the field annotation definition
         * TODO enhance each ProtectedField configuration with annotation metadata
         */
        private fun addSensitiveAnnotatedAttribute(
            annotation: Annotation,
            obj: Any,
            field: Field,
            attributes: MutableMap<String, Any>
        ) {
            val value = field.get(obj)
            when (annotation) {
                is HideDate -> {
                    if (value is String || value is Date || value is LocalDate) // TODO try to improve this line
                        attributes[field.name] = ProtectedField.ProtectedDateField(value)
                }
                is HideEmail -> {
                    if (value is String)
                        attributes[field.name] = ProtectedField.ProtectedEmailField(value)
                }
                is HideNumber -> {
                    if (value is Long)
                        attributes[field.name] = ProtectedField.ProtectedNumberField(value)
                }
                is HideText -> {
                    if (value is String)
                        attributes[field.name] = ProtectedField.ProtectedTextField(value)
                }
            }
        }
    }

    override fun toString() = getObjectInnerContent(attributes)

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
