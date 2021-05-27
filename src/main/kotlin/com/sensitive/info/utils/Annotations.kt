package com.sensitive.info.utils

/*
* Debe ser agregada a lo largo todos las capas de objetos si hay algun hijo con data que se precise protejer
 */
@Target(AnnotationTarget.CLASS)
annotation class Sensitive

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class HideDate(val pattern: String = "MM/dd/yyyy")

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class HideText(val shadow: Long = 2)

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class HideEmail

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class HideNumber(val shadow: Long = 2)
