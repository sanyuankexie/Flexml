package com.guet.flexbox

import android.util.Log.*
import org.apache.commons.logging.Log


internal class AndroidLog(private val tag: String) : Log {
    override fun debug(message: Any) {
        d(tag, message.toString())
    }

    override fun debug(message: Any, t: Throwable) {
        d(tag, message.toString(), t)
    }

    override fun error(message: Any) {
        e(tag, message.toString())
    }

    override fun error(message: Any, t: Throwable) {
        e(tag, message.toString(), t)
    }

    override fun fatal(message: Any) {
        wtf(tag, message.toString())
    }

    override fun fatal(message: Any, t: Throwable) {
        wtf(tag, message.toString(), t)
    }

    override fun info(message: Any) {
        i(tag, message.toString())
    }

    override fun info(message: Any, t: Throwable) {
        i(tag, message.toString(), t)
    }

    override fun trace(message: Any) {
        i(tag, message.toString())
    }

    override fun trace(message: Any, t: Throwable) {
        i(tag, message.toString(), t)
    }

    override fun warn(message: Any) {
        w(tag, message.toString())
    }

    override fun warn(message: Any, t: Throwable) {
        w(tag, message.toString(), t)
    }

    override fun isDebugEnabled(): Boolean {
        return isLoggable(tag, DEBUG)
    }

    override fun isErrorEnabled(): Boolean {
        return isLoggable(tag, ERROR)
    }

    override fun isFatalEnabled(): Boolean {
        return isLoggable(tag, ERROR)
    }

    override fun isInfoEnabled(): Boolean {
        return isLoggable(tag, INFO)
    }

    override fun isTraceEnabled(): Boolean {
        return isLoggable(tag, INFO)
    }

    override fun isWarnEnabled(): Boolean {
        return isLoggable(tag, WARN)
    }
}