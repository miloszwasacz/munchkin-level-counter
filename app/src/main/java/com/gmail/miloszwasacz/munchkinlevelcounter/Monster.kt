package com.gmail.miloszwasacz.munchkinlevelcounter


class Monster(var name: String, value: Int = 1) : BaseItem(value) {
    override fun toString(): String {
        return "Monster [name=$name, value=$value, isA=$isA]"
    }
    init {
        isA = "Monster"
    }
}