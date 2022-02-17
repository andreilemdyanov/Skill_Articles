package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(subStr: String, ignoreCase: Boolean = true): List<Int> {
    if (this == null) return emptyList()
    var result = 0
    val list = mutableListOf<Int>()
    do {
        result = this.indexOf(subStr, result, ignoreCase)
        if (result == -1 || result == 0) break
        list.add(result)
        result += subStr.length
    } while (this.length > result)
    return list
}