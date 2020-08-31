package core.logic

interface Serializer<T> {
    fun toJson(obj: T): String
    fun fromJson(string: String): T
}