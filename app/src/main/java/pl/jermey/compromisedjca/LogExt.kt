package pl.jermey.compromisedjca

import android.util.Log

fun Any.logD(message: String) {
    Log.d(logTag, message)
}

inline val Any.logTag: String
    get() {
        val tag = this::class.java.simpleName
        if (tag.isEmpty()) return "AnonymousObject"
        return tag
    }