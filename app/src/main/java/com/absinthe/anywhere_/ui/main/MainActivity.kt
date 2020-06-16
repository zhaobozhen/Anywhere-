package com.absinthe.anywhere_.ui.main

import android.annotation.SuppressLint
import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.adapter.page.PageListAdapter
import com.absinthe.anywhere_.adapter.page.PageTitleNode
import com.absinthe.anywhere_.adapter.page.PageTitleProvider
import com.absinthe.anywhere_.constants.*
import com.absinthe.anywhere_.constants.GlobalValues.setsCategory
import com.absinthe.anywhere_.databinding.ActivityMainBinding
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.services.BackupIntentService
import com.absinthe.anywhere_.services.overlay.CollectorService
import com.absinthe.anywhere_.transformer.CategoryCardTransformer
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.ui.list.AppListActivity
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.ui.settings.SettingsActivity
import com.absinthe.anywhere_.ui.setup.SetupActivity
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.CipherUtils.decrypt
import com.absinthe.anywhere_.utils.ClipboardUtil
import com.absinthe.anywhere_.utils.ClipboardUtil.clearClipboard
import com.absinthe.anywhere_.utils.ClipboardUtil.getClipBoardText
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.manager.DialogManager.showAddPageDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showAdvancedCardSelectDialog
import com.absinthe.anywhere_.utils.manager.IzukoHelper.isHitagi
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.home.DrawerRecyclerView
import com.absinthe.anywhere_.view.home.FabBuilder.build
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.gson.Gson
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.microsoft.appcenter.analytics.Analytics
import it.sephiroth.android.library.xtooltip.ClosePolicy.Companion.TOUCH_ANYWHERE_CONSUME
import it.sephiroth.android.library.xtooltip.Tooltip
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class MainActivity : BaseActivity() {

    private val viewModel by viewModels<AnywhereViewModel>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mDrawerRecyclerView: DrawerRecyclerView
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mObserver: Observer<List<PageEntity>?>

    private var isBound = false
    private var collectorService: CollectorService? = null
    private var mToggle: ActionBarDrawerToggle? = null

    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = (service as CollectorService.CollectorBinder).service
            collectorService?.startCollector()
            ActivityUtils.startHomeActivity()
        }

    }

    init {
        isPaddingToolbar = !GlobalValues.isMd2Toolbar
    }

    override fun setViewBinding() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FIRST_GUIDE)) {
            finish()
            startActivity(Intent(this, SetupActivity::class.java))
        }

        window.apply {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            sharedElementsUseOverlay = false
        }
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        super.onCreate(savedInstanceState)

        initObserver()
        getAnywhereIntent(intent)
        backupIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        Settings.setTheme(GlobalValues.darkMode)
        getClipBoardText(this, object : ClipboardUtil.Function {
            override fun invoke(text: String) {
                if (text.contains(URLManager.ANYWHERE_SCHEME)) {
                    processUri(text.toUri())
                    clearClipboard(this@MainActivity)
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getAnywhereIntent(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loadBackground(GlobalValues.backgroundUri)
        mToggle?.onConfigurationChanged(newConfig)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (GlobalValues.actionBarType == Const.ACTION_BAR_TYPE_LIGHT
                || UiUtils.isDarkMode(this) && GlobalValues.backgroundUri.isEmpty()) {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_LIGHT)
        } else {
            UiUtils.tintToolbarIcon(this, menu, mToggle, Const.ACTION_BAR_TYPE_DARK)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.toolbar_sort -> {
                val popup = PopupMenu(this, findViewById(R.id.toolbar_sort))
                popup.menuInflater
                        .inflate(R.menu.sort_menu, popup.menu)

                if (popup.menu is MenuBuilder) {
                    (popup.menu as MenuBuilder).setOptionalIconsVisible(true)
                }

                when (GlobalValues.sortMode) {
                    Const.SORT_MODE_TIME_DESC -> popup.menu.getItem(0).isChecked = true
                    Const.SORT_MODE_TIME_ASC -> popup.menu.getItem(1).isChecked = true
                    Const.SORT_MODE_NAME_DESC -> popup.menu.getItem(2).isChecked = true
                    Const.SORT_MODE_NAME_ASC -> popup.menu.getItem(3).isChecked = true
                    else -> popup.menu.getItem(0).isChecked = true
                }

                popup.setOnMenuItemClickListener { popupItem: MenuItem ->
                    when (popupItem.itemId) {
                        R.id.sort_by_time_desc -> GlobalValues.sortMode = Const.SORT_MODE_TIME_DESC
                        R.id.sort_by_time_asc -> GlobalValues.sortMode = Const.SORT_MODE_TIME_ASC
                        R.id.sort_by_name_desc -> GlobalValues.sortMode = Const.SORT_MODE_NAME_DESC
                        R.id.sort_by_name_asc -> GlobalValues.sortMode = Const.SORT_MODE_NAME_ASC
                        R.id.sort -> {
                            mBinding.viewPager.isUserInputEnabled = false
                            CategoryCardFragment.currentReference?.get()?.sort()
                        }
                        R.id.multi_select -> {
                            mBinding.viewPager.isUserInputEnabled = false
                            CategoryCardFragment.currentReference?.get()?.multiSelect()
                        }
                    }
                    if (popupItem.itemId == R.id.sort_by_time_desc ||
                            popupItem.itemId == R.id.sort_by_time_asc ||
                            popupItem.itemId == R.id.sort_by_name_desc ||
                            popupItem.itemId == R.id.sort_by_name_asc) {
                        CategoryCardFragment.currentReference?.get()?.refreshSortMode()
                    }
                    true
                }
                popup.show()
            }
            R.id.toolbar_done -> {
                CategoryCardFragment.currentReference?.get()?.editDone()
                mBinding.viewPager.isUserInputEnabled = true
            }
            R.id.toolbar_delete -> {
                CategoryCardFragment.currentReference?.get()?.deleteSelected()
            }
        }

        return if (mToggle?.onOptionsItemSelected(item) == true) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mBinding.drawer.isDrawerVisible(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START)
        } else {
            backupIfNeeded()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun initView() {
        super.initView()

        GlobalValues.backgroundUri.apply {
            if (isNotEmpty()) {
                loadBackground(this)
                UiUtils.setAdaptiveActionBarTitleColor(this@MainActivity, supportActionBar)
                UiUtils.setActionBarTransparent(this@MainActivity)
            }
        }
        initFab()

        AnywhereApplication.sRepository.allPageEntities.observe(this, Observer {
            if (it.isNotEmpty()) {
                mBinding.viewPager.apply {
                    adapter = object : FragmentStateAdapter(this@MainActivity) {
                        override fun getItemCount(): Int {
                            return it.size.coerceAtLeast(1)
                        }

                        override fun createFragment(position: Int): Fragment {
                            return CategoryCardFragment.newInstance(it[position].title)
                        }
                    }

                    // 当ViewPager切换页面时，改变页码
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            setsCategory(it[position].title, position)
                        }
                    })

                    setPageTransformer(CategoryCardTransformer())
                    setCurrentItem(GlobalValues.currentPage, false)
                }
            }
        })

        if (GlobalValues.isMd2Toolbar) {
            val marginHorizontal = resources.getDimension(R.dimen.toolbar_margin_horizontal).toInt()
            val marginVertical = resources.getDimension(R.dimen.toolbar_margin_vertical).toInt()
            val newLayoutParams = mBinding.toolbar.layoutParams as ConstraintLayout.LayoutParams
            newLayoutParams.apply {
                rightMargin = marginHorizontal
                leftMargin = newLayoutParams.rightMargin
                topMargin = BarUtils.getStatusBarHeight()
                bottomMargin = marginVertical
                height = ConvertUtils.dp2px(55f)
            }
            mBinding.toolbar.apply {
                layoutParams = newLayoutParams
                contentInsetStartWithNavigation = 0
                UiUtils.drawMd2Toolbar(this@MainActivity, this, 3)
            }
        }

        supportActionBar?.let {
            if (GlobalValues.isPages) {
                mToggle = ActionBarDrawerToggle(this, mBinding.drawer, mBinding.toolbar,
                        R.string.drawer_open, R.string.drawer_close)

                if (GlobalValues.actionBarType == Const.ACTION_BAR_TYPE_DARK) {
                    mToggle!!.drawerArrowDrawable.color = Color.BLACK
                } else {
                    mToggle!!.drawerArrowDrawable.color = Color.WHITE
                }

                it.setDisplayHomeAsUpEnabled(true)
                mBinding.drawer.addDrawerListener(mToggle!!)
                mToggle!!.syncState()
                AnywhereApplication.sRepository.allAnywhereEntities
                        .observe(this, Observer<List<AnywhereEntity?>?> { initDrawer(mBinding.drawer) })
            } else {
                it.setHomeButtonEnabled(false)
                it.setDisplayHomeAsUpEnabled(false)
                mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    private fun initDrawer(drawer: DrawerLayout) {
        mDrawerRecyclerView = drawer.findViewById(R.id.rv_pages)
        val adapter = PageListAdapter()
        val touchCallBack = ItemTouchCallBack().apply {
            setOnItemTouchListener(adapter)
        }
        mItemTouchHelper = ItemTouchHelper(touchCallBack).apply {
            attachToRecyclerView(null)
        }

        adapter.setOnItemChildClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->

            if (view.id == R.id.iv_entry) {
                drawer.closeDrawer(GravityCompat.START)

                (adapter.getItem(position) as PageTitleNode?)?.let { titleNode ->
                    AnywhereApplication.sRepository.allPageEntities.value?.find { it.title == titleNode.title }?.let { pe ->
                        lifecycleScope.launch(Dispatchers.Default) {
                            delay(300)

                            withContext(Dispatchers.Main) {
                                mBinding.viewPager.setCurrentItem(AnywhereApplication.sRepository.allPageEntities.value!!.indexOf(pe), true)
                                setsCategory(pe.title, position)
                            }
                        }
                    }
                }
            }
        }

        AnywhereApplication.sRepository.allPageEntities.observe(this, Observer { pageEntities: List<PageEntity>? ->
            pageEntities?.let { setupDrawerData(adapter, it) }
        })

        mDrawerRecyclerView.apply {
            this.adapter = adapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        val ibAdd: ImageButton = drawer.findViewById(R.id.ib_add)
        val ibPageSort: ImageButton = drawer.findViewById(R.id.ib_sort_page)
        val ibDone: ImageButton = drawer.findViewById(R.id.ib_done)

        ibAdd.setOnClickListener {
            if (isHitagi) {
                showAddPageDialog(this@MainActivity, DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                    if (which == 0) {
                        viewModel.addPage()
                    } else {
                        try {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "text/html"
                            }
                            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
                            setDocumentResultListener(object : OnDocumentResultListener {
                                override fun onResult(uri: Uri) {
                                    viewModel.addWebPage(uri, intent)
                                }
                            })
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            ToastUtil.makeText(R.string.toast_no_document_app)
                        }
                    }
                })
            } else {
                viewModel.addPage()
            }
        }
        ibPageSort.setOnClickListener {
            ibPageSort.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            var i = 0
            while (i < adapter.data.size) {
                adapter.collapse(i)
                i++
            }

            PageTitleProvider.isEditMode = true
            mDrawerRecyclerView.isEditMode = true
            mItemTouchHelper.attachToRecyclerView(mDrawerRecyclerView)

            ibAdd.visibility = View.GONE
            ibPageSort.visibility = View.GONE
            ibDone.visibility = View.VISIBLE
        }
        ibDone.setOnClickListener {
            ibDone.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            PageTitleProvider.isEditMode = false
            mDrawerRecyclerView.isEditMode = false
            mItemTouchHelper.attachToRecyclerView(null)

            ibAdd.visibility = View.VISIBLE
            ibPageSort.visibility = View.VISIBLE
            ibDone.visibility = View.GONE

            val list: List<BaseNode> = adapter.data
            val map = HashMap<String, Int>()
            var i = 1
            for (node in list) {
                if (node is PageTitleNode) {
                    map[node.title] = i
                    i++
                }
            }

            AnywhereApplication.sRepository.allPageEntities.value?.let {
                for (pe in it) {
                    map[pe.title]?.let { entity ->
                        pe.priority = entity
                        AnywhereApplication.sRepository.updatePage(pe)
                    }
                }
            }
        }
    }

    private fun setupDrawerData(adapter: PageListAdapter, pageEntities: List<PageEntity>) = lifecycleScope.launch(Dispatchers.IO) {
        val list: MutableList<BaseNode> = ArrayList()
        for (pe in pageEntities) {
            list.add(viewModel.getEntity(pe.title))
        }
        withContext(Dispatchers.Main) {
            adapter.setNewInstance(list)
        }
    }

    private fun initObserver() {
        mObserver = Observer<List<PageEntity>?> { pageEntities ->
            if (pageEntities == null) return@Observer

            AnywhereApplication.sRepository.allPageEntities.removeObserver(mObserver)

            if (pageEntities.isEmpty() && !isPageInit) {
                val pe = PageEntity.Builder().apply {
                    title = GlobalValues.category
                    priority = 1
                }
                AnywhereApplication.sRepository.insertPage(pe)
                isPageInit = true
            }
        }

        AnywhereApplication.sRepository.allPageEntities.observe(this, mObserver)
        AnywhereApplication.sRepository.allAnywhereEntities.observe(this, Observer {
            if (Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FIRST_GUIDE) && !Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.CONVERT_TYPE_TO_V2)) {
                viewModel.convertAnywhereTypeV2()
                Once.markDone(OnceTag.CONVERT_TYPE_TO_V2)
            }
        })

        viewModel.background.observe(this, Observer { s: String ->
            GlobalValues.backgroundUri = s

            if (s.isNotEmpty()) {
                loadBackground(GlobalValues.backgroundUri)
                UiUtils.setAdaptiveActionBarTitleColor(this, supportActionBar)
                UiUtils.setActionBarTransparent(this)
            }
        })
        viewModel.shouldShowFab.observe(this, Observer {
            mBinding.fab.isVisible = it
        })
    }

    private fun initFab() {
        build(this, mBinding.fab)
        mBinding.fab.mainFab.transitionName = "item_container"
        mBinding.fab.setOnActionSelectedListener { actionItem: SpeedDialActionItem ->
            when (actionItem.id) {
                R.id.fab_url_scheme -> {
                    viewModel.setUpUrlScheme(this)
                    Analytics.trackEvent(EventTag.FAB_URL_SCHEME_CLICK)
                }
                R.id.fab_activity_list -> {
                    startActivity(Intent(this, AppListActivity::class.java))
                    Analytics.trackEvent(EventTag.FAB_ACTIVITY_LIST_CLICK)
                }
                R.id.fab_collector -> {
                    viewModel.startCollector(this, object : AnywhereViewModel.OnStartCollectorListener {
                        override fun onStart() {
                            if (isBound) {
                                collectorService?.startCollector()
                                ActivityUtils.startHomeActivity()
                            } else {
                                bindService(Intent(this@MainActivity, CollectorService::class.java), conn, Context.BIND_AUTO_CREATE)
                            }
                        }
                    })
                    Analytics.trackEvent(EventTag.FAB_COLLECTOR_CLICK)
                }
                R.id.fab_qr_code_collection -> {
                    startActivity(Intent(this, QRCodeCollectionActivity::class.java))
                    Analytics.trackEvent(EventTag.FAB_QR_CODE_COLLECTION_CLICK)
                }
                R.id.fab_advanced -> showAdvancedCardSelectDialog(this)
                else -> return@setOnActionSelectedListener false
            }
            mBinding.fab.close()
            true
        }

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_TIP)) {
            showFirstTip(mBinding.fab)

            viewModel.insert(AnywhereEntity.Builder().apply {
                appName = getString(R.string.help_card_title)
                type = AnywhereType.Card.URL_SCHEME
                param1 = URLManager.OLD_DOCUMENT_PAGE
            })

            Once.markDone(OnceTag.FAB_TIP)
        }
    }

    private fun getAnywhereIntent(intent: Intent) {
        val action = intent.action
        Timber.d("action = %s", action)

        if (action == null || action == Intent.ACTION_VIEW) {
            intent.data?.let {
                Timber.d("Received Url = %s", it.toString())
                Timber.d("Received path = %s", it.path)
                processUri(it)
            }
        } else if (action == Intent.ACTION_SEND) {
            val sharing = intent.getStringExtra(Intent.EXTRA_TEXT)
            viewModel.setUpUrlScheme(this, AppTextUtils.parseUrlFromSharingText(sharing))
        }
    }

    private fun processUri(uri: Uri) {
        if (uri.host == URLManager.URL_HOST) {
            val param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1) ?: ""
            val param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2) ?: ""
            val param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3) ?: ""
            val type = uri.getQueryParameter(Const.INTENT_EXTRA_TYPE) ?: return

            when (type.toInt()) {
                AnywhereType.Card.URL_SCHEME -> {
                    viewModel.setUpUrlScheme(this, param1)
                }
                AnywhereType.Card.ACTIVITY -> {
                    val appName = AppUtils.getAppName(param1) ?: ""
                    val ae = AnywhereEntity.Builder().apply {
                        this.appName = appName
                        this.param1 = param1
                        this.param2 = param2
                        this.param3 = param3
                        this.type = AnywhereType.Card.ACTIVITY
                    }
                    startActivity(Intent(this, EditorActivity::class.java).apply {
                        putExtra(EXTRA_ENTITY, ae)
                        putExtra(EXTRA_EDIT_MODE, false)
                    })
                }
                AnywhereType.Card.SHELL -> {
                    val ae = AnywhereEntity.Builder().apply {
                        this.appName = "New Shell"
                        this.param1 = param1
                        this.param2 = param2
                        this.param3 = param3
                        this.type = AnywhereType.Card.SHELL
                    }
                    startActivity(Intent(this, EditorActivity::class.java).apply {
                        putExtra(EXTRA_ENTITY, ae)
                        putExtra(EXTRA_EDIT_MODE, false)
                    })
                }
            }
        } else if (uri.host == URLManager.CARD_SHARING_HOST) {
            uri.path?.let {
                if (uri.toString().isNotEmpty()) {
                    val encrypted = it.substring(1)
                    val decrypted = decrypt(encrypted)

                    startActivity(Intent(this, EditorActivity::class.java).apply {
                        putExtra(EXTRA_ENTITY, Gson().fromJson(decrypted, AnywhereEntity::class.java))
                        putExtra(EXTRA_EDIT_MODE, false)
                    })
                }
            }
        }
    }

    private fun loadBackground(url: String) {
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mBinding.ivBack)
    }

    private fun showFirstTip(target: View) {
        target.post {
            Tooltip.Builder(this@MainActivity)
                    .anchor(target, 0, 0, false)
                    .text(getText(R.string.first_launch_guide_title))
                    .closePolicy(TOUCH_ANYWHERE_CONSUME)
                    .maxWidth(ConvertUtils.dp2px(150f))
                    .create()
                    .show(target, Tooltip.Gravity.LEFT, true)
        }
    }

    private fun backupIfNeeded() {
        if (GlobalValues.needBackup && GlobalValues.isAutoBackup) {
            startService(Intent(this, BackupIntentService::class.java))
            GlobalValues.needBackup = false
        }
    }

    companion object {
        private var isPageInit = false
    }
}