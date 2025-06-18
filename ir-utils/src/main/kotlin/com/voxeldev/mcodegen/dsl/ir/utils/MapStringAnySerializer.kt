package com.voxeldev.mcodegen.dsl.ir.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

object MapStringAnySerializer : KSerializer<Map<String, Any>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("LanguageProps") {
            mapSerialDescriptor(
                String.serializer().descriptor,
                JsonElement.serializer().descriptor
            )
        }

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        require(encoder is JsonEncoder)

        val obj = buildJsonObject {
            value.forEach { (k, v) ->
                put(k, v.toJsonElement())
            }
        }
        encoder.encodeJsonElement(obj)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any> {
        require(decoder is JsonDecoder)
        val obj = decoder.decodeJsonElement().jsonObject
        return obj.mapValues { (_, je) -> je.toKotlinAny() }
    }

    private fun Any.toJsonElement(): JsonElement = when (this) {
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is List<*> -> JsonArray(map { (it ?: JsonNull).toJsonElement() })
        is Map<*, *> -> buildJsonObject {
            for ((k, v) in this@toJsonElement)
                put(k.toString(), (v ?: JsonNull).toJsonElement())
        }

        else -> JsonPrimitive(toString())
    }

    private fun JsonElement.toKotlinAny(): Any = when (this) {
        is JsonPrimitive -> when {
            isString -> content
            booleanOrNull != null -> boolean
            longOrNull != null -> long
            doubleOrNull != null -> double
            else -> content
        }

        is JsonArray -> map { it.toKotlinAny() }
        is JsonObject -> mapValues { it.value.toKotlinAny() }
    }
}
