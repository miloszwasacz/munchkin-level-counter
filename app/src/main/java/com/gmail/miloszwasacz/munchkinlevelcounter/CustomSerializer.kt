package com.gmail.miloszwasacz.munchkinlevelcounter

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.*





class CustomSerializer: JsonSerializer<ArrayList<BaseItem>?> {
    companion object {
        private val map = TreeMap<String, Class<*>>()

        init {
            map["BaseItem"] = BaseItem::class.java
            map["Player"] = Player::class.java
            map["Monster"] = Monster::class.java
        }
    }

    override fun serialize(src: ArrayList<BaseItem>?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {
        return if(src == null) null
        else {
            val jsonArray = JsonArray()
            for(baseItem in src) {
                val c = map[baseItem.isA] ?: throw RuntimeException("Unknown class: " + baseItem.isA)
                jsonArray.add(context.serialize(baseItem, c))
            }
            jsonArray
        }
    }
}