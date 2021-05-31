package com.sensitive.info.appender

import com.sensitive.info.DynamicClass
import com.sensitive.info.utils.Sensitive
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.appender.AppenderLoggingException
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.impl.MutableLogEvent
import org.apache.logging.log4j.message.ReusableMessageFactory
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

@Plugin(name = "CustomAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
class CustomAppender(
    name: String?,
    layout: Layout<*>,
    filter: Filter?
) : AbstractAppender(
    name,
    filter,
    layout,
    false,
    null
) { //    ConsoleAppender(name, layout filter, null, false, null) {

//    private val eventMap: ConcurrentMap<String, LogEvent> = ConcurrentHashMap()

    private val logger: Logger = LogManager.getLogger(javaClass.name)

    companion object {
        private val rwLock: ReadWriteLock = ReentrantReadWriteLock()
        private val readLock: Lock = rwLock.readLock()

        @PluginFactory
        @JvmStatic
        fun createAppender(
            @PluginAttribute("name") name: String,
            // @PluginElement("Layout") Layout layout,
            @PluginElement("Layout") layout: Layout<*>, // TODO check layout functionality
            @PluginElement("Filter") filter: Filter?
        ): CustomAppender {
//            if (name == null) {
//                LOGGER.error("No name provided for StubAppender")
//                return null
//            }
//
//            val manager: StubManager = StubManager.getStubManager(name) ?: return null
//            if (layout == null) {
//                layout = PatternLayout.createDefaultLayout()
//            }
//            return StubAppender(name, layout, filter, ignoreExceptions, manager)
            // return new TestAppender(name, layout, filter);
            return CustomAppender(name, layout, filter)
        }
    }

    override fun append(event: LogEvent) {
        readLock.lock()
        try {
            val eventWithSensitiveArgumentsCasted = castEventToSensitiveLogEvent(event)
            val bytes = layout.toByteArray(eventWithSensitiveArgumentsCasted)
            System.out.write(bytes)
        } catch (ex: Exception) {
            if (!ignoreExceptions()) {
                throw AppenderLoggingException(ex)
            }
        } finally {
            readLock.unlock()
        }
    }

    /**
     * Analyze event arguments and map @sensitive classes
     */
    private fun castEventToSensitiveLogEvent(event: LogEvent): LogEvent {
        val parameters = mutableListOf<Any>()
        event.message.parameters.forEach {
            // if some of the arguments has @Sensitive annotation
            if (it::class.annotations.filterIsInstance<Sensitive>().isNotEmpty()) {
                // build dynamicClass
                // TODO to improve performance KAPT Kotlin Annotation Processing ... (basically, adding static code in compiling time.
                parameters.add(DynamicClass.of(it))
            } else {
                parameters.add(it)
            }
        }
        // replace arguments within LogEvent
        if (parameters.filterIsInstance<DynamicClass>().isNotEmpty()) {
            val message = if (parameters.size == 1)
                ReusableMessageFactory.INSTANCE.newMessage(event.message.format, parameters[0])
            else
                ReusableMessageFactory.INSTANCE.newMessage(event.message.format, parameters)
            //  println("parameters: $parameters")
            (event as MutableLogEvent).message = message
        }
        return event
    }
}
