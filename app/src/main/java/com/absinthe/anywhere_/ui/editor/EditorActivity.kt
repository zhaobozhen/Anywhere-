package com.absinthe.anywhere_.ui.editor

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivityEditorBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.impl.SchemeEditorFragment
import com.absinthe.anywhere_.utils.UiUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

const val EXTRA_COLOR = "EXTRA_COLOR"
const val EXTRA_ENTITY = "EXTRA_ENTITY"

class EditorActivity : BaseActivity() {

    private lateinit var binding: ActivityEditorBinding
    private lateinit var bottomDrawerBehavior: BottomSheetBehavior<FrameLayout>
    private val color by lazy { intent.getIntExtra(EXTRA_COLOR, 0) }
    private val entity by lazy { intent.getParcelableExtra(EXTRA_ENTITY) as AnywhereEntity? }

    override fun setViewBinding() {
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.bar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (entity == null) finish()
        initTransition()
        super.onCreate(savedInstanceState)
        setUpBottomDrawer()

        supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainerView.id, SchemeEditorFragment.newInstance(entity!!))
                .commitNow()
    }

    override fun onBackPressed() {
        if (bottomDrawerBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
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
                duration = 300L
            }
        }
        findViewById<View>(android.R.id.content).transitionName = getString(R.string.trans_item_container)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    }

    private fun setUpBottomDrawer() {
        bottomDrawerBehavior = BottomSheetBehavior.from(binding.bottomDrawer)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.bar.setNavigationOnClickListener { bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED) }
        binding.fab.backgroundTintList = ColorStateList.valueOf(color)

        binding.fab.imageTintList = if (UiUtils.isLightColor(color)) {
            ColorStateList.valueOf(Color.BLACK)
        } else {
            ColorStateList.valueOf(Color.WHITE)
        }
    }
}
