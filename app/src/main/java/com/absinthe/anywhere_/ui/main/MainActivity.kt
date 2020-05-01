package com.absinthe.anywhere_.ui.main

import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.adapter.page.PageListAdapter
import com.absinthe.anywhere_.adapter.page.PageTitleNode
import com.absinthe.anywhere_.adapter.page.PageTitleProvider
import com.absinthe.anywhere_.constants.*
import com.absinthe.anywhere_.constants.GlobalValues.clearActionBarType
import com.absinthe.anywhere_.constants.GlobalValues.setsCategory
import com.absinthe.anywhere_.databinding.ActivityMainBinding
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.PageEntity
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.services.CollectorService
import com.absinthe.anywhere_.ui.fragment.AdvancedCardSelectDialogFragment
import com.absinthe.anywhere_.ui.fragment.AdvancedCardSelectDialogFragment.OnClickItemListener
import com.absinthe.anywhere_.ui.list.AppListActivity
import com.absinthe.anywhere_.ui.main.WelcomeFragment.Companion.newInstance
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.utils.*
import com.absinthe.anywhere_.utils.AnimationUtil.showAndHiddenAnimation
import com.absinthe.anywhere_.utils.CipherUtils.decrypt
import com.absinthe.anywhere_.utils.ClipboardUtil.clearClipboard
import com.absinthe.anywhere_.utils.ClipboardUtil.getClipBoardText
import com.absinthe.anywhere_.utils.manager.DialogManager.showAddPageDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showAdvancedCardSelectDialog
import com.absinthe.anywhere_.utils.manager.IzukoHelper.isHitagi
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.FabBuilder.build
import com.absinthe.anywhere_.view.editor.AnywhereEditor
import com.absinthe.anywhere_.view.editor.Editor
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
import com.google.gson.Gson
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.microsoft.appcenter.analytics.Analytics
import it.sephiroth.android.library.xtooltip.ClosePolicy.Companion.TOUCH_ANYWHERE_CONSUME
import it.sephiroth.android.library.xtooltip.Tooltip
import jonathanfinerty.once.Once
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewModel: AnywhereViewModel
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mObserver: Observer<List<PageEntity>?>

    private var isBound = false
    private var collectorService: CollectorService? = null

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
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AnywhereViewModel::class.java)

        initObserver()

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE)) {
            mBinding.fab.visibility = View.GONE

            val welcomeFragment = newInstance()
            viewModel.fragment.value = welcomeFragment
        } else {
            val mainFragment = MainFragment.newInstance(GlobalValues.category)
            viewModel.fragment.value = mainFragment

            getAnywhereIntent(intent)
            backupIfNeeded()
        }
    }

    override fun onResume() {
        super.onResume()
        Settings.setTheme(GlobalValues.darkMode)
        getClipBoardText(this, object : ClipboardUtil.Function {
            override fun invoke(text: String) {
                if (text.contains(URLManager.ANYWHERE_SCHEME)) {
                    processUri(Uri.parse(text))
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
        mToggle.onConfigurationChanged(newConfig)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mBinding.drawer.isDrawerVisible(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START)
        } else {
            backupIfNeeded()
            super.onBackPressed()
        }
    }

    override fun initView() {
        super.initView()
        val actionBar = supportActionBar

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
            mBinding.toolbar.layoutParams = newLayoutParams
            mBinding.toolbar.contentInsetStartWithNavigation = 0
            UiUtils.drawMd2Toolbar(this, mBinding.toolbar, 3)
        }
        if (actionBar != null) {
            mToggle = ActionBarDrawerToggle(this, mBinding.drawer, mBinding.toolbar,
                    R.string.drawer_open, R.string.drawer_close)

            if (GlobalValues.isPages) {
                if (GlobalValues.actionBarType == Const.ACTION_BAR_TYPE_DARK) {
                    mToggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
                } else {
                    mToggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
                }
                actionBar.setDisplayHomeAsUpEnabled(true)
                mBinding.drawer.addDrawerListener(mToggle)
                mToggle.syncState()
                AnywhereApplication.sRepository.allAnywhereEntities
                        .observe(this, Observer<List<AnywhereEntity?>?> { initDrawer(mBinding.drawer) })
            } else {
                actionBar.setHomeButtonEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(false)
                mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    private fun initDrawer(drawer: DrawerLayout) {
        val recyclerView: RecyclerView = drawer.findViewById(R.id.rv_pages)
        val adapter = PageListAdapter()
        adapter.setOnItemChildClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->

            if (view.id == R.id.iv_entry) {
                drawer.closeDrawer(GravityCompat.START)

                (adapter.getItem(position) as PageTitleNode?)?.let {
                    ListUtils.getPageEntityByTitle(it.title)?.let { pe ->
                        GlobalScope.launch(Dispatchers.Default) {
                            delay(300)

                            withContext(Dispatchers.Main) {
                                if (pe.type == AnywhereType.CARD_PAGE) {
                                    viewModel.fragment.value = MainFragment.newInstance(pe.title)
                                    setsCategory(pe.title, position)
                                } else if (pe.type == AnywhereType.WEB_PAGE) {
                                    viewModel.fragment.value = WebviewFragment.newInstance(pe.extra)
                                }

                                pe.backgroundUri?.let { uri ->
                                    if (uri.isNotEmpty()) {
                                        clearActionBarType()
                                        viewModel.background.value = uri
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        AnywhereApplication.sRepository.allPageEntities.observe(this, Observer { pageEntities: List<PageEntity>? ->
            pageEntities?.let { setupDrawerData(adapter, it) }
        })

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
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

            for (i in (adapter.data.indices)) {
                try {
                    adapter.collapse(i)
                } catch (ignore: IndexOutOfBoundsException) {
                }
            }
            PageTitleProvider.isEditMode = true
            val touchCallBack = ItemTouchCallBack().apply {
                setOnItemTouchListener(adapter)
            }
            mItemTouchHelper = ItemTouchHelper(touchCallBack).apply {
                attachToRecyclerView(recyclerView)
            }

            ibAdd.visibility = View.GONE
            ibPageSort.visibility = View.GONE
            ibDone.visibility = View.VISIBLE
        }
        ibDone.setOnClickListener {
            ibDone.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            PageTitleProvider.isEditMode = false
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

    private fun setupDrawerData(adapter: PageListAdapter, pageEntities: List<PageEntity>) {
        val list: MutableList<BaseNode> = ArrayList()
        for (pe in pageEntities) {
            list.add(viewModel.getEntity(pe.title))
        }
        adapter.setNewInstance(list)
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

        viewModel.background.observe(this, Observer { s: String ->
            GlobalValues.backgroundUri = s

            if (s.isNotEmpty()) {
                loadBackground(GlobalValues.backgroundUri)
                UiUtils.setAdaptiveActionBarTitleColor(this, supportActionBar, UiUtils.getActionBarTitle())
                UiUtils.setActionBarTransparent(this)
            }
        })
        viewModel.background.value = GlobalValues.backgroundUri

        viewModel.fragment.observe(this, Observer { fragment: Fragment? ->
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .replace(mBinding.fragmentContainerView.id, fragment!!)
                    .commitNow()
            if (fragment is MainFragment) {
                initFab()
                if (mBinding.fab.visibility == View.GONE) {
                    showAndHiddenAnimation(mBinding.fab, AnimationUtil.AnimationState.STATE_SHOW, 300)
                }
            } else {
                if (mBinding.fab.visibility == View.VISIBLE) {
                    showAndHiddenAnimation(mBinding.fab, AnimationUtil.AnimationState.STATE_GONE, 300)
                }
            }
        })
        viewModel.shouldShowFab.observe(this, Observer {
            mBinding.fab.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun initFab() {
        build(this, mBinding.fab)
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
                R.id.fab_advanced -> showAdvancedCardSelectDialog(this, object : OnClickItemListener {
                    override fun onClick(item: Int) {
                        when (item) {
                            AdvancedCardSelectDialogFragment.ITEM_ADD_IMAGE -> {
                                viewModel.openImageEditor(this@MainActivity, true)
                                Analytics.trackEvent(EventTag.FAB_IMAGE_CLICK)
                            }
                            AdvancedCardSelectDialogFragment.ITEM_ADD_SHELL -> {
                                viewModel.openShellEditor(this@MainActivity, true)
                                Analytics.trackEvent(EventTag.FAB_SHELL_CLICK)
                            }
                            AdvancedCardSelectDialogFragment.ITEM_ADD_SWITCH_SHELL -> {
                                viewModel.openSwitchShellEditor(this@MainActivity, true)
                                Analytics.trackEvent(EventTag.FAB_SWITCH_SHELL_CLICK)
                            }
                        }
                    }

                })
                else -> return@setOnActionSelectedListener false
            }
            mBinding.fab.close()
            true
        }

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FAB_GUIDE)) {
            showFirstTip(mBinding.fab)

            viewModel.insert(AnywhereEntity.Builder().apply {
                appName = getString(R.string.help_card_title)
                type = AnywhereType.URL_SCHEME
                param1 = URLManager.OLD_DOCUMENT_PAGE
            })

            Once.markDone(OnceTag.FAB_GUIDE)
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
            viewModel.setUpUrlScheme(this, TextUtils.parseUrlFromSharingText(sharing))
        }
    }

    private fun processUri(uri: Uri) {
        if (uri.host == URLManager.URL_HOST) {
            val param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1)
            val param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2)
            val param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3)

            if (param1 != null && param2 != null && param3 != null) {
                if (param2.isEmpty() && param3.isEmpty()) {
                    viewModel.setUpUrlScheme(this, param1)
                } else {
                    val appName: String = AppUtils.getAppName(param1)
                    var exported = 0

                    if (UiUtils.isActivityExported(this, ComponentName(param1,
                                    if (param2[0] == '.') param1 + param2 else param2))) {
                        exported = 100
                    }
                    val ae = AnywhereEntity.Builder().apply {
                        this.appName = appName
                        this.param1 = param1
                        this.param2 = param2
                        this.param3 = param3
                        this.type = AnywhereType.ACTIVITY + exported
                    }
                    val editor: Editor<*> = AnywhereEditor(this)
                            .item(ae)
                            .isEditorMode(false)
                            .isShortcut(false)
                            .build()
                    editor.show()
                }
            }
        } else if (uri.host == URLManager.CARD_SHARING_HOST) {
            if (uri.path != null && uri.toString().isNotEmpty()) {
                val encrypted = uri.path!!.substring(1)
                val decrypted = decrypt(encrypted)
                val ae = Gson().fromJson(decrypted, AnywhereEntity::class.java)
                val editor: Editor<*> = AnywhereEditor(this)
                        .item(ae)
                        .isEditorMode(false)
                        .isShortcut(false)
                        .build()
                editor.show()
            }
        }
    }

    private fun loadBackground(url: String) {
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(mBinding.ivBack)
    }

    private fun showFirstTip(target: View) {
        target.post {
            val tooltip = Tooltip.Builder(this@MainActivity)
                    .anchor(target, 0, 0, false)
                    .text(getText(R.string.first_launch_guide_title))
                    .closePolicy(TOUCH_ANYWHERE_CONSUME)
                    .maxWidth(ConvertUtils.dp2px(150f))
                    .create()
            tooltip.show(target, Tooltip.Gravity.LEFT, true)
        }
    }

    private fun backupIfNeeded() {
        if (GlobalValues.needBackup) {
            StorageUtils.webdavBackup()
            GlobalValues.needBackup = false
        }
    }

    companion object {
        private var isPageInit = false
    }
}