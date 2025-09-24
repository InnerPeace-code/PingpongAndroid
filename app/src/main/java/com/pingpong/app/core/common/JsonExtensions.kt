package com.pingpong.app.core.common

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

fun JsonElement?.asJsonObjectOrNull(): JsonObject? = this as? JsonObject

fun JsonElement?.asJsonArrayOrNull(): JsonArray? = this as? JsonArray

fun JsonElement?.asStringOrNull(): String? = (this as? JsonPrimitive)?.contentOrNull

fun JsonElement?.asLongOrNull(): Long? = (this as? JsonPrimitive)?.longOrNull

fun JsonElement?.asIntOrNull(): Int? = (this as? JsonPrimitive)?.intOrNull

fun JsonElement?.asDoubleOrNull(): Double? = (this as? JsonPrimitive)?.doubleOrNull

fun JsonElement?.asBooleanOrNull(): Boolean? = (this as? JsonPrimitive)?.booleanOrNull

fun JsonObject.stringOrNull(key: String): String? = this[key].asStringOrNull()

fun JsonObject.longOrNull(key: String): Long? = this[key].asLongOrNull()

fun JsonObject.intOrNull(key: String): Int? = this[key].asIntOrNull()

fun JsonObject.doubleOrNull(key: String): Double? = this[key].asDoubleOrNull()

fun JsonObject.booleanOrNull(key: String): Boolean? = this[key].asBooleanOrNull()

fun JsonObject.jsonObjectOrNull(key: String): JsonObject? = this[key]?.asJsonObjectOrNull()

fun JsonObject.jsonArrayOrNull(key: String): JsonArray? = this[key]?.asJsonArrayOrNull()

fun JsonArray.mapObjects(): List<JsonObject> = this.mapNotNull { it.asJsonObjectOrNull() }

fun JsonArray.mapStrings(): List<String> = this.mapNotNull { it.asStringOrNull() }

fun JsonObject.getString(key: String, defaultValue: String = ""): String = stringOrNull(key) ?: defaultValue

fun JsonObject.getInt(key: String, defaultValue: Int = 0): Int = intOrNull(key) ?: defaultValue

fun JsonObject.getLong(key: String, defaultValue: Long = 0L): Long = longOrNull(key) ?: defaultValue

fun JsonObject.getDouble(key: String, defaultValue: Double = 0.0): Double = doubleOrNull(key) ?: defaultValue

fun JsonObject.getBoolean(key: String, defaultValue: Boolean = false): Boolean = booleanOrNull(key) ?: defaultValue

fun JsonElement?.isNullOrEmptyArray(): Boolean = when (this) {
    null, JsonNull -> true
    is JsonArray -> this.isEmpty()
    else -> false
}
