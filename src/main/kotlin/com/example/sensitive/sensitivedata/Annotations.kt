package com.example.sensitive.sensitivedata

@Target(AnnotationTarget.FIELD)
annotation class HideDate(val pattern: String = "MM/dd/yyyy")

@Target(AnnotationTarget.FIELD)
annotation class HideText(val shadow: Long = 2)

@Target(AnnotationTarget.FIELD)
annotation class HideEmail

@Target(AnnotationTarget.FIELD)
annotation class HideNumber(val shadow: Long = 2)
