package com.absinthe.anywhere_.ui.main

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.adapter.SpacesItemDecoration
import com.absinthe.anywhere_.adapter.card.*
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.FragmentCategoryCardBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.AppUtils.updateWidget
import com.absinthe.anywhere_.utils.manager.ActivityStackManager.topActivity
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.google.android.material.card.MaterialCardView
import java.lang.ref.WeakReference

const val BUNDLE_CATEGORY = "CATEGORY"

class CategoryCardFragment : Fragment() {

    private val category by lazy { arguments?.getString(BUNDLE_CATEGORY) ?: GlobalValues.category }
    private val viewModel by activityViewModels<AnywhereViewModel>()
    private lateinit var decoration: SpacesItemDecoration

    private lateinit var binding: FragmentCategoryCardBinding
    private lateinit var adapter: BaseCardAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val listObserver = Observer<List<AnywhereEntity>> { list ->
        if (!refreshLock) {
            adapter.setDiffNewData(
                    if (category == AnywhereType.Category.DEFAULT_CATEGORY) {
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

    override fun onResume() {
        super.onResume()
        currentReference = WeakReference<CategoryCardFragment>(this)

        if (GlobalValues.shortcutListChanged) {
            adapter.notifyDataSetChanged()
            GlobalValues.shortcutListChanged = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager(newConfig)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.toolbar_settings).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_sort).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_done).isVisible = adapter.mode != ADAPTER_MODE_NORMAL
        menu.findItem(R.id.toolbar_delete).isVisible = adapter.mode == ADAPTER_MODE_SELECT
        super.onPrepareOptionsMenu(menu)
    }

    fun sort() {
        adapter.mode = ADAPTER_MODE_SORT
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        requireActivity().invalidateOptionsMenu()
        binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    fun multiSelect() {
        adapter.mode = ADAPTER_MODE_SELECT
        requireActivity().invalidateOptionsMenu()
        binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    fun refreshSortMode() {
        AnywhereApplication.sRepository.refresh()
        AnywhereApplication.sRepository.allAnywhereEntities.observe(this, listObserver)
    }

    fun editDone() {
        if (adapter.mode == ADAPTER_MODE_SORT) {
            adapter.mode = ADAPTER_MODE_NORMAL

            itemTouchHelper.attachToRecyclerView(null)
            requireActivity().invalidateOptionsMenu()

            adapter.updateSortedList()
            GlobalValues.sortMode = Const.SORT_MODE_TIME_DESC
        } else if (adapter.mode == ADAPTER_MODE_SELECT) {
            resetSelectState()
            adapter.clearSelect()
            adapter.mode = ADAPTER_MODE_NORMAL
            requireActivity().invalidateOptionsMenu()
        }
        binding.recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    fun deleteSelected() {
        DialogManager.showDeleteSelectCardDialog(requireContext(), DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            adapter.deleteSelect()
            resetSelectState()
        })
    }

    private fun initView() {
        setHasOptionsMenu(true)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        decoration = SpacesItemDecoration(resources.getDimension(R.dimen.cardview_item_margin).toInt())

        adapter = if (GlobalValues.isStreamCardMode) {
            if (GlobalValues.isStreamCardModeSingleLine) {
                BaseCardAdapter(LAYOUT_MODE_STREAM_SINGLE_LINE)
            } else {
                BaseCardAdapter(LAYOUT_MODE_STREAM)
            }
        } else {
            decoration = SpacesItemDecoration(resources.getDimension(R.dimen.cardview_margin_parent_horizontal).toInt() / 2)
            BaseCardAdapter(LAYOUT_MODE_NORMAL)
        }

        adapter.apply {
            setDiffCallback(DiffListCallback())
            setOnItemClickListener { _, view, i ->
                clickItem(view, i)
            }
            setOnItemLongClickListener { _, view, i ->
                longClickItem(view, i)
            }
        }

        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.addItemDecoration(decoration)

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
        binding.recyclerView.removeItemDecoration(decoration)
        setupRecyclerView()

        AnywhereApplication.sRepository.allAnywhereEntities.value?.let { list ->
            adapter.setDiffNewData(
                    if (category == AnywhereType.Category.DEFAULT_CATEGORY) {
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
        var currentReference: WeakReference<CategoryCardFragment>? = null
    }
}