package com.github.panpf.activity.monitor.sample


inline fun Any?.toSimpleString(): String {
    return if (this != null) this.javaClass.simpleName + "@" + Integer.toHexString(this.hashCode()) else "null"
}

inline fun Int?.toHexString(): String {
    return if (this != null) Integer.toHexString(this.hashCode()) else "null"
}

