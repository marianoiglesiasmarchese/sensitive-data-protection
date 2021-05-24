package com.sensitive.info.appender

import java.io.Serializable

object ModelHelper {

    inline fun <reified T : Serializable> mergeFields(from: T, to: T) {
        from::class.java.declaredFields.forEach { field ->
            val isLocked = field.isAccessible
            field.isAccessible = true
            if (field.name != "parameters")
                field.set(to, field.get(from))
            field.isAccessible = isLocked
        }
    }

}