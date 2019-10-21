package com.guet.flexbox.build

object Func {
    @JvmName("check")
    @JvmStatic
    fun check(o: Any?): Boolean {
        return when (o) {
            is String -> o.isNotEmpty()
            is Collection<*> -> !o.isEmpty()
            is Number -> o.toInt() != 0
            else -> o != null
        }
    }
}
