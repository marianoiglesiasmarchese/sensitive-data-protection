package com.example.sensitive.sensitivedata

@Retention(AnnotationRetention.SOURCE)
@Target(*[AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD])
annotation class Redacted
