package com.gmail.miloszwasacz.munchkinlevelcounter


class Player(var name: String, value: Int = 1) : BaseItem(value) {
    override fun toString(): String {
        return "Player [name=$name, value=$value, isA=$isA]"
    }
    init {
        isA = "Player"
    }
}