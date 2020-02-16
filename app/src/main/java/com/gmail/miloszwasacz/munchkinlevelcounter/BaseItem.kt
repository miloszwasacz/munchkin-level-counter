package com.gmail.miloszwasacz.munchkinlevelcounter

open class BaseItem(var value: Int) {
    override fun toString(): String {
        return "BaseClass [value=$value, isA=$isA]"
    }
    var isA = "BaseItem"
}
