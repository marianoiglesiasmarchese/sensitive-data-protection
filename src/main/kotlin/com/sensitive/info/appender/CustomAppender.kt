package com.sensitive.info.appender

import com.sensitive.info.DynamicClass
import com.sensitive.info.utils.Sensitive
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
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
import org.apache.logging.log4j.message.ReusableParameterizedMessage


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
            @PluginElement("Layout") layout: Layout<*>,
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
//            if (it::class.annotations.contains(Sensitive::class.java)) {
                // build dynamicClass
                /**
                 *  TODO DynamicClass has some issues to be solved but up to this point the main goal is to force
                 *      the message format as described down below
                 */
                parameters.add(DynamicClass.of(it))
            } else {
                parameters.add(it)
            }
        }
        // replace arguments within LogEvent
//        println(event.message.format)
//        val str: StringBuilder = if (event.message.format.isNullOrBlank()) {
//            StringBuilder()
//        } else {
//            StringBuilder(event.message.format)
//        }
//        val castedEvent = MutableLogEvent(str, parameters.toTypedArray())
//        var message = SimpleMessageFactory.INSTANCE.newMessage(event.message.format, parameters)

//        message = SimpleMessage(event.message.format)
//        message.formatTo(str)
//        var message = MutableLogEvent(str, parameters.toTypedArray()).message

//        ModelHelper.mergeFields(event.message, message)

        /**
         *  TODO despite the parameters manipulation the message.formattedMessage is still the previous one, so we have to force the formatting.
         *      In addition, it seems that the message.format is erased after swapProperties()
         */
        if (parameters.filterIsInstance<DynamicClass>().isNotEmpty()) {
            val message = ReusableMessageFactory.INSTANCE.newMessage(event.message.format, parameters)
            println(parameters)
            (event as MutableLogEvent).message = message
        }

//        println(event.message)
//        println(event.message.format)
//        println(event.message.parameters.size)
//        println(event.message.formattedMessage)

//        Log4jLogEvent.newBuilder().setLevel(event.level).setMessage(message).build()
//        public Log4jLogEvent(String loggerName, Marker marker, String loggerFQCN, StackTraceElement source, Level level, Message message, List<Property> properties, Throwable t) {
//        return Log4jLogEvent.newBuilder().setLevel(event.level).setMessage(message).build()
        return event
    }
}

