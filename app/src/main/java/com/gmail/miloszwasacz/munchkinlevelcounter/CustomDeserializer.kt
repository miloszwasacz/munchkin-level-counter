package com.gmail.miloszwasacz.munchkinlevelcounter

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class CustomDeserializer: JsonDeserializer<List<BaseItem>> {
    companion object {
        private val map = TreeMap<String, Class<*>>()

        init {
            map["BaseItem"] = BaseItem::class.java
            map["Player"] = Player::class.java
            map["Monster"] = Monster::class.java
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<BaseItem> {
        val list = ArrayList<BaseItem>()
        val ja: JsonArray = json.asJsonArray
        for(je in ja) {
            val type: String = je.asJsonObject.get("isA").asString
            val c = map[type] ?: throw RuntimeException("Unknown class: $type")
            list.add(context.deserialize<BaseItem>(je, c))
        }
        return list
    }
}