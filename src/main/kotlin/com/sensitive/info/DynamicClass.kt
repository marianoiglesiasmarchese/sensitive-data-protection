package com.sensitive.info

import com.sensitive.info.utils.HideDate
import com.sensitive.info.utils.HideEmail
import com.sensitive.info.utils.HideNumber
import com.sensitive.info.utils.HideText
import com.sensitive.info.utils.ProtectedProperty
import com.sensitive.info.utils.Sensitive
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

data class DynamicClass(
    private val attributes: MutableMap<String, Any> = LinkedHashMap()
) {

    companion object {
        private val CONVERTIBLE_TYPES =
            listOf(Long::class.createType(), String::class.createType(), Date::class.createType())
        private const val CLASS_NAME_PROPERTY = "class_name_prop"
        private val SENSITIVE_ANNOTATIONS =
            listOf(HideDate::class.java, HideEmail::class.java, HideText::class.java, HideNumber::class.java)

        /**
         * previamente tuvimos que evaluar en el CustomAppender que la clase tenía la annotation
         */
        fun of(obj: Any): DynamicClass {
            val dynamicClass = DynamicClass()
            // recorrer properties del objeto
            obj::class.declaredMemberProperties.forEach { field ->
                // aquellos que no sean de primer nivel (son un objeto), pedir la creación del mapa
                if (field.returnType !in CONVERTIBLE_TYPES) {
                    // of(obj.property_not_convertible)
                    of(dynamicClass.attributes, field.name, field.getter.call(obj)!!)
                } else {
                    // aquellos que son de primer nivel (CONVERTIBLE_TYPES)
                    // TODO it seems that the annotations are not detected
//                    println("field.annotations = ${field.annotations}")
//                    println("field.getter.annotations = ${field.getter.annotations}")
                    val sensitiveAnnotated: Annotation? =
                        field.annotations.find { it in SENSITIVE_ANNOTATIONS } // TODO check types mismatch
                    // si tiene alguna annotation,
                    sensitiveAnnotated?.also { annotation ->
                        //  agregarlos a attributes (con atributos que sean atributos protegidos con las configuracion que amerite (ProtectedProperty.kt))
                        addSensitiveAnnotatedAttribute(annotation, field, dynamicClass.attributes)
                    } ?: dynamicClass.attributes.put(
                        field.name,
                        field.getter.call(obj)!!
                    ) // si no tiene annotation, //  agregarlos a attributes
                }
            }
            // agregar el class name al mapa this.javaClass.simpleName y diferenciarlo con el nombre CLASS_NAME_PROPERTY
            dynamicClass.attributes[CLASS_NAME_PROPERTY] = obj.javaClass.simpleName
            return dynamicClass
        }

        private fun of(attributes: MutableMap<String, Any>, fieldName: String, obj: Any) {
            val attribute = if (obj::class.annotations.filterIsInstance<Sensitive>().isNotEmpty()) {
                mutableMapOf(fieldName to of(obj))
            } else {
                mutableMapOf(fieldName to obj)
            }
            attributes.putAll(attribute)
        }

        private fun addSensitiveAnnotatedAttribute(
            annotation: Annotation,
            field: KProperty<*>,
            attributes: MutableMap<String, Any>
        ) {
            val value: Any?
            when (annotation) {
                is HideDate -> {
                    value = field.getter.call(field)
                    if (value in listOf(String::class, Date::class))
                        attributes[field.name] = ProtectedProperty.ProtectedDateProperty(value)
                }
                is HideEmail -> {
                    value = field.getter.call(field)
                    if (value is String)
                        attributes[field.name] = ProtectedProperty.ProtectedEmailProperty(value)
                }
                is HideNumber -> {
                    value = field.getter.call(field)
                    if (value is Long)
                        attributes[field.name] = ProtectedProperty.ProtectedNumberProperty(value)
                }
                is HideText -> {
                    value = field.getter.call(field)
                    if (value is String)
                        attributes[field.name] = ProtectedProperty.ProtectedTextProperty(value)
                }
            }
        }
    }

    override fun toString() = getObjectInnerContent(attributes)

    private fun getObjectInnerContent(toPrint: Map<String, Any>): String {
        var className = ""
        val objAttributes: MutableList<String> = ArrayList()
        toPrint.forEach { (key, value) ->
            if (value is Map<*, *>) {
                getObjectInnerContent(value as Map<String, Any>)
            } else {
                if (key == CLASS_NAME_PROPERTY) {
                    className = value.toString()
                } else {
                    objAttributes.add("$key=${value.toString()}, ")
                }
            }
        }
        var result = "$className("
        objAttributes.map { result = result.plus(it) }
        result = result.substring(0, result.length - 2).plus(")")
        return result // TODO check order of the attributes
    }

}