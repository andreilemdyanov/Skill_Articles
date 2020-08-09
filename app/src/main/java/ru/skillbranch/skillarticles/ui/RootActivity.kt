package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {
    private lateinit var viewmodel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()
        setupBottomBar()
        setupSubmenu()

        val vmFactory = ViewModelFactory("0")
        viewmodel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
        viewmodel.observeState(this) {
            renderUI(it)
        }
        viewmodel.observeNotifications(this) {
            renderNotification(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewmodel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewmodel.handleSearchMode(false)
                return true
            }
        })
        searchView.queryHint = ""
        if (viewmodel.currentState.isSearch) {
            searchItem.expandActionView()
            searchView.setQuery(viewmodel.currentState.searchQuery, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewmodel.handleSearch(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(coordinator_container, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(bottombar)
            .setActionTextColor(getColor(R.color.color_accent_dark))

        when (notify) {
            is Notify.TextMessage -> {
            }
            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler?.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    private fun setupSubmenu() {
        btn_text_up.setOnClickListener { viewmodel.handleUpText() }
        btn_text_down.setOnClickListener { viewmodel.handleDownText() }
        switch_mode.setOnClickListener { viewmodel.handleNightMode() }

    }

    private fun setupBottomBar() {
        btn_like.setOnClickListener { viewmodel.handleLike() }
        btn_bookmark.setOnClickListener { viewmodel.handleBookmark() }
        btn_share.setOnClickListener { viewmodel.handleShare() }
        btn_settings.setOnClickListener { viewmodel.handleToggleMenu() }
    }

    private fun renderUI(data: ArticleState) {
        btn_settings.isChecked = data.isShowMenu
        if (data.isShowMenu) submenu.open() else submenu.close()

        btn_like.isChecked = data.isLike
        btn_bookmark.isChecked = data.isBookmark

        switch_mode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            tv_text_content.textSize = 18f
            btn_text_up.isChecked = true
            btn_text_down.isChecked = false
        } else {
            tv_text_content.textSize = 14f
            btn_text_up.isChecked = false
            btn_text_down.isChecked = true
        }

        tv_text_content.text =
            if (data.isLoadingContent) "loading" else data.content.first() as String

        toolbar.title = data.title ?: "loading"
        toolbar.subtitle = data.category ?: "loading"
        if (data.categoryIcon != null) toolbar.logo = getDrawable(data.categoryIcon as Int)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }
}