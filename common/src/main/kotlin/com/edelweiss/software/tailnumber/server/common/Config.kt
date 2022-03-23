package com.edelweiss.software.tailnumber.server.common

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

object Config {
    private val config = systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("tailnumber.properties") overriding
            ConfigurationProperties.fromResource("defaults.properties")

    operator fun <T> get(key: Key<T>) = config[key]

    fun <T> getValue(key: String, parse: (PropertyLocation, String) -> T) = this[Key(key, parse)]

    fun getString(key: String) = getValue(key, stringType)

    fun getInt(key: String) = getValue(key, intType)

    fun getLong(key: String) = getValue(key, longType)

    fun getDouble(key: String) = getValue(key, doubleType)

    fun getBoolean(key: String) = getValue(key, booleanType)

    fun <T> getList(key: String, separator: String, parse: (PropertyLocation, String) -> T) =
        getValue(key, listType(parse, Regex("${Regex.escape(separator)}\\s*")))

    fun getStringList(key: String, separator: String = ",") : List<String> =
        getList(key, separator) { _, s -> s }

}