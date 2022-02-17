package ru.skillbranch.skillarticles.ui

import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.BottombarData
import ru.skillbranch.skillarticles.viewmodels.SubMenuData

interface IArticleView {
    fun setupSubmenu()
    fun setupBottomBar()
    fun renderBottombar(data: BottombarData)
    fun renderSubmenu(data: SubMenuData)
    fun renderUI(data: ArticleState)
    fun setupToolbar()
    fun renderSearchResult(searchResult: List<Pair<Int, Int>>)
    fun renderSearchPosition(searchPosition: Int)
    fun clearSearchResult()
    fun showSearchBar(resultsCount: Int, searchPosition: Int)
    fun hideSearchBar()
}