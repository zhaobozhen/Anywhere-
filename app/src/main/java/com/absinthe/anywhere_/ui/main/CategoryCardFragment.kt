package com.absinthe.anywhere_.ui.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.adapter.card.*
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.GlobalValues.sortMode
import com.absinthe.anywhere_.databinding.FragmentCategoryCardBinding
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.ui.settings.SettingsActivity
import com.absinthe.anywhere_.utils.AppUtils.updateWidget
import com.absinthe.anywhere_.utils.manager.ActivityStackManager.topActivity
import com.absinthe.anywhere_.utils.manager.DialogManager.showDeleteSelectCardDialog
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.card.MaterialCardView

const val BUNDLE_CATEGORY = "CATEGORY"

class CategoryCardFragment : Fragment() {

    private val category by lazy { arguments?.getString(BUNDLE_CATEGORY) ?: GlobalValues.category }
    private val viewModel by activityViewModels<AnywhereViewModel>()
    private lateinit var binding: FragmentCategoryCardBinding
    private lateinit var adapter: BaseCardAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val listObserver = Observer<List<AnywhereEntity>> { list ->
        if (!refreshLock) {
            adapter.setDiffNewData(
                    if (category == AnywhereType.DEFAULT_CATEGORY) {
                        list.filter { it.category.isEmpty() || it.category == this.category }.toMutableList()
                    } else {
                        list.filter { it.category == this.category }.toMutableList()
                    }
            )
            if (!binding.recyclerView.canScrollVertically(-1)) {
                //Fix Fab cannot be shown after deleting an Anywhere-
                viewModel.shouldShowFab.value = true
            }
        }
        updateWidget(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryCardBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        GlobalValues.cardModeLiveData.observe(viewLifecycleOwner, Observer {
            refreshRecyclerView()
        })
        AnywhereApplication.sRepository.allAnywhereEntities.observe(viewLifecycleOwner, listObserver)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.toolbar_settings).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_sort).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_done).isVisible = adapter.mode != ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_delete).isVisible = adapter.mode == ADAPTER_MODE_SELECT
        super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
            }
            R.id.toolbar_sort -> {
                val popup = PopupMenu(requireContext(), requireActivity().findViewById(R.id.toolbar_sort))
                popup.menuInflater
                        .inflate(R.menu.sort_menu, popup.menu)

                if (popup.menu is MenuBuilder) {
                    (popup.menu as MenuBuilder).setOptionalIconsVisible(true)
                }

                when (sortMode) {
                    Const.SORT_MODE_TIME_DESC -> popup.menu.getItem(0).isChecked = true
                    Const.SORT_MODE_TIME_ASC -> popup.menu.getItem(1).isChecked = true
                    Const.SORT_MODE_NAME_DESC -> popup.menu.getItem(2).isChecked = true
                    Const.SORT_MODE_NAME_ASC -> popup.menu.getItem(3).isChecked = true
                    else -> popup.menu.getItem(0).isChecked = true
                }

                popup.setOnMenuItemClickListener { popupItem: MenuItem ->
                    when (popupItem.itemId) {
                        R.id.sort_by_time_desc -> sortMode = Const.SORT_MODE_TIME_DESC
                        R.id.sort_by_time_asc -> sortMode = Const.SORT_MODE_TIME_ASC
                        R.id.sort_by_name_desc -> sortMode = Const.SORT_MODE_NAME_DESC
                        R.id.sort_by_name_asc -> sortMode = Const.SORT_MODE_NAME_ASC
                        R.id.sort -> {
                            adapter.mode = ADAPTER_MODE_SORT
                            itemTouchHelper.attachToRecyclerView(binding.recyclerView)
                            requireActivity().invalidateOptionsMenu()
                            binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                        R.id.multi_select -> {
                            adapter.mode = ADAPTER_MODE_SELECT
                            requireActivity().invalidateOptionsMenu()
                            binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    }
                    if (popupItem.itemId == R.id.sort_by_time_desc ||
                            popupItem.itemId == R.id.sort_by_time_asc ||
                            popupItem.itemId == R.id.sort_by_name_desc ||
                            popupItem.itemId == R.id.sort_by_name_asc) {
                        AnywhereApplication.sRepository.refresh()
                        AnywhereApplication.sRepository.allAnywhereEntities.observe(this, listObserver)
                    }
                    true
                }
                popup.show()
            }
            R.id.toolbar_done -> {
                if (adapter.mode == ADAPTER_MODE_SORT) {
                    adapter.mode = ADAPTER_MODE_NORMAL

                    itemTouchHelper.attachToRecyclerView(null)
                    requireActivity().invalidateOptionsMenu()

                    adapter.updateSortedList()
                    sortMode = Const.SORT_MODE_TIME_DESC
                } else if (adapter.mode == ADAPTER_MODE_SELECT) {
                    resetSelectState()
                    adapter.clearSelect()
                    adapter.mode = ADAPTER_MODE_NORMAL
                    requireActivity().invalidateOptionsMenu()
                }
                binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
            R.id.toolbar_delete -> {
                showDeleteSelectCardDialog(requireContext(), DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                    adapter.deleteSelect()
                    resetSelectState()
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        setHasOptionsMenu(true)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = if (GlobalValues.isStreamCardMode) {
            if (GlobalValues.isStreamCardModeSingleLine) {
                BaseCardAdapter(R.layout.item_stream_card_single_line)
            } else {
                BaseCardAdapter(R.layout.item_stream_card_view)
            }
        } else {
            BaseCardAdapter(R.layout.item_card_view)
        }

        adapter.apply {
            setDiffCallback(DiffListCallback())
            setOnItemClickListener { _: BaseQuickAdapter<*, *>, view: View, i: Int ->
                clickItem(view, i)
            }
            setOnItemLongClickListener { _: BaseQuickAdapter<*, *>, view: View, i: Int ->
                longClickItem(view, i)
            }
        }

        binding.recyclerView.adapter = this.adapter

        setRecyclerViewLayoutManager(resources.configuration)

        itemTouchHelper = ItemTouchHelper(ItemTouchCallBack().apply {
            setOnItemTouchListener(adapter)
        }).apply {
            attachToRecyclerView(null)
        }
    }

    private fun resetSelectState() {
        for (pos in 0 until adapter.itemCount) {
            binding.recyclerView.layoutManager?.findViewByPosition(pos)?.let {
                it.scaleX = 1.0f
                it.scaleY = 1.0f
                (it as MaterialCardView).isChecked = false
            }
        }
    }

    private fun refreshRecyclerView() {
        setupRecyclerView()

        AnywhereApplication.sRepository.allAnywhereEntities.value?.let { list ->
            adapter.setDiffNewData(
                    if (category == AnywhereType.DEFAULT_CATEGORY) {
                        list.filter { it.category.isEmpty() || it.category == this.category }.toMutableList()
                    } else {
                        list.filter { it.category == this.category }.toMutableList()
                    }
            )
        }
    }

    private fun setRecyclerViewLayoutManager(configuration: Configuration) {
        binding.recyclerView.layoutManager = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (GlobalValues.isStreamCardMode) {
                WrapContentStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            } else {
                WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        } else {
            if (GlobalValues.isStreamCardMode) {
                WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            } else {
                WrapContentLinearLayoutManager(topActivity)
            }
        }
    }

    companion object {
        fun newInstance(category: String): CategoryCardFragment {
            return CategoryCardFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_CATEGORY, category)
                }
            }
        }

        var refreshLock = false
    }
}