package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> {
    val resultList = mutableListOf<MutableList<Pair<Int, Int>>>()
    bounds.forEach { pair ->
        val sublist = mutableListOf<Pair<Int, Int>>()
        val range = pair.first..pair.second
        forEach {
            if (it.first in range && it.second in range)
                sublist.add(it)
            if (it.first in range && it.second !in range) {
                sublist.add(it.first to range.last)
            }
            if (it.first !in range && it.second in range) {
                if (range.first != it.second) sublist.add(range.first to it.second)
            }
        }
        resultList.add(sublist)
    }
    return resultList
}