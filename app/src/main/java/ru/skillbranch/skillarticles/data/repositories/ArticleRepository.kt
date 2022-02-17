package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.*

object ArticleRepository {
    private val local = LocalDataHolder
    private val network = NetworkDataHolder
    private val prefs: PrefManager = PrefManager()

    fun loadArticleContent(articleId: String): LiveData<List<String>?> {
        return network.loadArticleContent(articleId)
    }

    fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId)
    }

    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId)
    }

    fun getAppSettings(): LiveData<AppSettings> = prefs.settings

    fun updateSettings(appSettings: AppSettings) {
        prefs.isBigText = appSettings.isBigText
        prefs.isDarkMode = appSettings.isDarkMode
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }
}