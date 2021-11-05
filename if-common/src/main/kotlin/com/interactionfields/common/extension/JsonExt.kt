package com.interactionfields.common.extension

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.interactionfields.common.extension.DateTimeExt.YYYY_MM_DD_HH_MM_SS
import org.ktorm.jackson.KtormModule
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utilities about the JSON.
 *
 * @author Ashinch
 * @date 2021/07/31
 */
object JsonExt {

    private val FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)

    /**
     * Generate default [ObjectMapper].
     */
    val DEFAULT_MAPPER = ObjectMapper().apply {
        registerModule(KtormModule())
        registerModule(JavaTimeModule().apply {
            addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(FORMATTER))
            addSerializer(LocalDate::class.java, LocalDateSerializer(FORMATTER))
            addSerializer(LocalTime::class.java, LocalTimeSerializer(FORMATTER))
        })
        setTimeZone(TimeZone.getDefault())
        dateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * Generate complete serialization [ObjectMapper].
     */
    val COMPLETE_MAPPER = ObjectMapper().apply {
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        dateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * Returns the converted JSON format [String] of the [Any] object.
     */
    fun Any.toJson(customMapper: ObjectMapper? = null): String =
        (customMapper ?: DEFAULT_MAPPER).run { writeValueAsString(this@toJson) }

    /**
     * Returns an [T] object converted from a [String] in JSON format.
     */
    fun <T> String.toObj(customMapper: ObjectMapper? = null): T =
        (customMapper ?: DEFAULT_MAPPER).run { readValue(this@toObj, object : TypeReference<T>() {}) }

    /**
     * Returns an [Any] object converted from a [String] in JSON format.
     */
    fun String.toObj(type: Class<*>, customMapper: ObjectMapper? = null): Any =
        (customMapper ?: DEFAULT_MAPPER).run { readValue(this@toObj, type) }

    /**
     * Returns an [T] object converted from a [InputStream] in JSON format.
     * [MismatchedInputException] is thrown when no content to map due to end-of-input.
     */
    @Throws(MismatchedInputException::class)
    fun <T> InputStream.toObj(customMapper: ObjectMapper? = null): T =
        (customMapper ?: DEFAULT_MAPPER).run { readValue(this@toObj, object : TypeReference<T>() {}) }

    /**
     * Returns an [Any] object converted from a [InputStream] in JSON format.
     */
    fun InputStream.toObj(type: Class<*>, customMapper: ObjectMapper? = null): Any =
        (customMapper ?: DEFAULT_MAPPER).run { readValue(this@toObj, type) }
}
