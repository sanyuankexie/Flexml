package android.util

@Suppress("FunctionName")
fun <K, V> ArrayMap(map: Map<K, V>): ArrayMap<K, V> {
    return ArrayMap<K, V>(map.size).apply {
        putAll(map)
    }
}