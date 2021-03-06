package com.sensitive.info.annotation

/**
 * This annotation should be tagged through all the path of classes that want to be protected
 */
@Target(AnnotationTarget.CLASS)
annotation class Sensitive

@Target(AnnotationTarget.FIELD)
annotation class HideDate(val pattern: String = "MM/dd/yyyy")

@Target(AnnotationTarget.FIELD)
annotation class HideText(val visibility: Long = 2)

@Target(AnnotationTarget.FIELD)
annotation class HideEmail

@Target(AnnotationTarget.FIELD)
annotation class HideNumber(val visibility: Long = 2)
