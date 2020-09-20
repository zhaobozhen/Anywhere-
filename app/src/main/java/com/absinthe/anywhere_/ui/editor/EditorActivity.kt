package com.absinthe.anywhere_.ui.editor

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.ActivityEditorBinding
import com.absinthe.anywhere_.listener.OnDocumentResultListener
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.absinthe.anywhere_.services.overlay.OverlayService
import com.absinthe.anywhere_.ui.dialog.EXTRA_FROM_WORKFLOW
import com.absinthe.anywhere_.ui.editor.impl.WorkflowEditorFragment
import com.absinthe.anywhere_.utils.*
import com.absinthe.anywhere_.utils.AppUtils.atLeastNMR1
import com.absinthe.anywhere_.utils.AppUtils.atLeastR
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.DialogManager.showAddShortcutDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showCannotAddShortcutDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showCreatePinnedShortcutDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showRemoveShortcutDialog
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.blankj.utilcode.util.PermissionUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import io.michaelrocks.paranoid.Obfuscate
import jonathanfinerty.once.Once

const val EXTRA_ENTITY = "EXTRA_ENTITY"
const val EXTRA_EDIT_MODE = "EXTRA_EDIT_MODE"

@Obfuscate
class EditorActivity : BaseActivity() {

    private lateinit var binding: ActivityEditorBinding
    private lateinit var bottomDrawerBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var editor: IEditor
    private lateinit var entity: AnywhereEntity

    private val _entity by lazy { intent.getParcelableExtra(EXTRA_ENTITY) as? AnywhereEntity }
    private val isEditMode by lazy { intent.getBooleanExtra(EXTRA_EDIT_MODE, false) }
    private val isFromWorkFlow by lazy { intent.getBooleanExtra(EXTRA_FROM_WORKFLOW, false) }

    override fun setViewBinding() {
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.bar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initTransition()

        if (_entity == null) {
            finish()
        } else {
            entity = _entity!!

            super.onCreate(savedInstanceState)
            setUpBottomDrawer()
        }
    }

    override fun onBackPressed() {
        if (bottomDrawerBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            setResult(Activity.RESULT_OK)
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (shouldShowMenu()) {
            if (isEditMode) {
                menuInflater.inflate(R.menu.editor_bottom_bar_edit_mode_menu, menu)
            } else {
                menuInflater.inflate(R.menu.editor_bottom_bar_menu, menu)
            }
        }
        return true
    }

    override fun initView() {
        super.initView()
        if (isEditMode) {
            binding.tvOpenUrl.apply {
                isVisible = true
                text = HtmlCompat.fromHtml(
                        String.format(getString(R.string.bsd_open_url),
                                entity.id.substring(entity.id.length - 4, entity.id.length)),
                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                setOnLongClickListener {
                    ClipboardUtil.put(this@EditorActivity, "anywhere://open?sid=${entity.id.substring(entity.id.length - 4, entity.id.length)}")
                    ToastUtil.makeText(R.string.toast_copied)
                    true
                }
            }
        } else {
            binding.tvOpenUrl.isGone = true
        }

        editor = EditorFactory.produce(entity.type)

        val fragment = editor as BaseEditorFragment
        fragment.apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_ENTITY, entity)
                putBoolean(EXTRA_EDIT_MODE, isEditMode)
                putBoolean(EXTRA_FROM_WORKFLOW, isFromWorkFlow)
            }
        }
        supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainerView.id, fragment)
                .commitNow()

        if (editor is WorkflowEditorFragment) {
            workflowResultItem.observe(this, {
                (editor as WorkflowEditorFragment).apply {
                    if (adapter.data.isNotEmpty()) {
                        adapter.setData(currentIndex, FlowStepBean(it, adapter.data[currentIndex].delay))
                    }
                }
            })
        }
    }

    private fun initTransition() {
        window.apply {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = 300L
            }
            sharedElementReturnTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = 250L
            }
        }
        findViewById<View>(android.R.id.content).transitionName = getString(R.string.trans_item_container)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    }

    private fun setUpBottomDrawer() {
        bottomDrawerBehavior = BottomSheetBehavior.from(binding.bottomDrawer)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.bar.apply {
            if (!isEditMode) {
                navigationIcon?.alpha = 64
                setNavigationOnClickListener(null)
            } else {
                navigationIcon?.alpha = 255
                setNavigationOnClickListener { bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED) }
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.trying_run -> { editor.tryRunning() }
                    R.id.overlay -> { startOverlay() }
                }
                true
            }
        }

        binding.fab.apply {
            val color = if (entity.color == 0) {
                ContextCompat.getColor(context, R.color.colorPrimary)
            } else {
                entity.color
            }
            backgroundTintList = ColorStateList.valueOf(color)

            imageTintList = if (UxUtils.isLightColor(color)) {
                ColorStateList.valueOf(Color.BLACK)
            } else {
                ColorStateList.valueOf(Color.WHITE)
            }

            setOnClickListener {
                if (editor.doneEdit()) {
                    onBackPressed()
                }
            }
        }

        binding.navigationView.apply {
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.add_shortcuts -> {
                        if (atLeastNMR1()) {
                            if (!GlobalValues.shortcutsList.contains(entity.id)) {
                                addShortcut(this@EditorActivity, entity)
                            } else {
                                removeShortcut(this@EditorActivity, entity)
                            }
                        }
                    }
                    R.id.add_home_shortcuts -> {
                        showCreatePinnedShortcutDialog(this@EditorActivity, entity)
                    }
                    R.id.delete -> {
                        DialogManager.showDeleteAnywhereDialog(this@EditorActivity, entity)
                    }
                    R.id.move_to_page -> {
                        DialogManager.showPageListDialog(this@EditorActivity, entity)
                    }
                    R.id.custom_color -> {
                        DialogManager.showColorPickerDialog(this@EditorActivity, entity)
                    }
                    R.id.share_card -> {
                        DialogManager.showCardSharingDialog(this@EditorActivity, AppTextUtils.genCardSharingUrl(entity))
                    }
                    R.id.custom_icon -> {
                        setDocumentResultListener(object : OnDocumentResultListener {
                            override fun onResult(uri: Uri) {
                                val ae = AnywhereEntity(entity).apply {
                                    iconUri = uri.toString()
                                }
                                AnywhereApplication.sRepository.update(ae)
                                onBackPressed()
                            }

                        })

                        try {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "image/*"
                            }
                            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            ToastUtil.makeText(R.string.toast_no_document_app)
                        }
                    }
                    R.id.restore_icon -> {
                        val ae = AnywhereEntity(entity).apply {
                            iconUri = ""
                        }
                        AnywhereApplication.sRepository.update(ae)
                        onBackPressed()
                    }
                }
                bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                true
            }

            menu.findItem(R.id.add_shortcuts)?.let {
                if (atLeastNMR1()) {
                    if (GlobalValues.shortcutsList.contains(entity.id)) {
                        binding.navigationView.apply {
                            menu.clear()
                            inflateMenu(R.menu.editor_added_shortcut_menu)
                        }
                    }
                } else {
                    it.isVisible = false
                }
            }

            menu.findItem(R.id.restore_icon)?.isVisible = entity.iconUri.isNotEmpty()
            menu.findItem(R.id.share_card)?.isVisible = entity.type != AnywhereType.Card.IMAGE && entity.type != AnywhereType.Card.FILE

            invalidate()
        }
    }

    private fun startOverlay() {
        if (PermissionUtils.isGrantedDrawOverlays()) {
            startOverlayImpl()

        } else {
            if (atLeastR()) {
                ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
            }
            PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    startOverlayImpl()
                }

                override fun onDenied() {}
            })
        }
    }

    private fun startOverlayImpl() {
        startService(Intent(this, OverlayService::class.java).apply {
            putExtra(OverlayService.ENTITY, entity)
        })
        finish()

        if (!Once.beenDone(OnceTag.OVERLAY_TIP)) {
            ToastUtil.makeText(R.string.toast_overlay_tip)
            Once.markDone(OnceTag.OVERLAY_TIP)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private fun addShortcut(context: Context, ae: AnywhereEntity) {
        if (ShortcutsUtils.SHORTCUT_MANAGER!!.dynamicShortcuts.size < 3) {
            val builder = AnywhereDialogBuilder(context)
            showAddShortcutDialog(context, builder, ae) {
                ShortcutsUtils.addShortcut(ae)
                onBackPressed()
            }
        } else {
            showCannotAddShortcutDialog(context) {
                ShortcutsUtils.addShortcut(ae)
                onBackPressed()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private fun removeShortcut(context: Context, ae: AnywhereEntity) {
        showRemoveShortcutDialog(context, ae) {
            ShortcutsUtils.removeShortcut(ae)
            onBackPressed()
        }
    }

    private fun shouldShowMenu(): Boolean {
        return entity.type != AnywhereType.Card.IMAGE &&
                entity.type != AnywhereType.Card.SWITCH_SHELL &&
                entity.type != AnywhereType.Card.FILE
    }

    companion object {
        var workflowResultItem: MutableLiveData<AnywhereEntity> = MutableLiveData()
    }
}
