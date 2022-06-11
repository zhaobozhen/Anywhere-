package com.absinthe.anywhere_.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.adapter.SpacesItemDecoration
import com.absinthe.anywhere_.adapter.card.*
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.FragmentCategoryCardBinding
import com.absinthe.anywhere_.extension.addSystemBarPaddingAsync
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.AppUtils.updateWidget
import com.absinthe.anywhere_.utils.doOnMainThreadIdle
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.libraries.utils.extensions.paddingEndCompat
import com.absinthe.libraries.utils.extensions.paddingStartCompat
import com.absinthe.libraries.utils.utils.XiaomiUtilities
import com.blankj.utilcode.util.Utils
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

const val BUNDLE_CATEGORY = "CATEGORY"

class CategoryCardFragment : Fragment() {

  private val category by lazy { arguments?.getString(BUNDLE_CATEGORY) ?: GlobalValues.category }
  private lateinit var decoration: SpacesItemDecoration

  private lateinit var binding: FragmentCategoryCardBinding
  private lateinit var adapter: BaseCardAdapter
  private lateinit var itemTouchHelper: ItemTouchHelper
  private var isFirstLoadItems = true

  private val listObserver = Observer<List<AnywhereEntity>> { list ->
    if (isFirstLoadItems) {
      isFirstLoadItems = false
      updateItems(list)
    } else {
      doOnMainThreadIdle({
        binding.root.postDelayed({ updateItems(list) }, 300)
      })
    }
  }
  private val cardObserver = Observer<Any> { refreshRecyclerView() }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentCategoryCardBinding.inflate(inflater, container, false)
    initView()
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    currentReference = WeakReference(this)

    if (GlobalValues.shortcutListChanged) {
      adapter.notifyDataSetChanged()
      GlobalValues.shortcutListChanged = false
    }
  }

  override fun onDetach() {
    super.onDetach()
    unregisterObservers()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    setRecyclerViewLayoutManager(binding.recyclerView, newConfig)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    menu.findItem(R.id.toolbar_settings).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
    menu.findItem(R.id.toolbar_sort).isVisible = adapter.mode == ADAPTER_MODE_NORMAL
    menu.findItem(R.id.toolbar_done).isVisible = adapter.mode != ADAPTER_MODE_NORMAL
    menu.findItem(R.id.toolbar_delete).isVisible = adapter.mode == ADAPTER_MODE_SELECT
    menu.findItem(R.id.toolbar_move).isVisible = adapter.mode == ADAPTER_MODE_SELECT
    menu.findItem(R.id.toolbar_create_sc).isVisible =
      adapter.mode == ADAPTER_MODE_SELECT && XiaomiUtilities.isMIUI()

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
    observeEntitiesList()
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
    DialogManager.showDeleteSelectCardDialog(requireContext()) {
      adapter.deleteSelect()
      resetSelectState()
    }
  }

  fun moveSelected() {
    DialogManager.showPageListDialog(requireContext()) {
      adapter.moveSelect(it)
    }
  }

  fun createShortcutSelected() {
    DialogManager.showMultiSelectCreatingShortcutDialog(requireContext()) {
      GlobalScope.launch(Dispatchers.IO) {
        adapter.createShortcutSelect()

        withContext(Dispatchers.Main) {
          resetSelectState()
        }
      }
    }
  }

  private fun initView() {
    setHasOptionsMenu(true)
    setupRecyclerView()
    initObservers()
  }

  private fun setupRecyclerView() {
    decoration = SpacesItemDecoration(resources.getDimension(R.dimen.cardview_item_margin).toInt())

    when (GlobalValues.cardMode) {
      Const.PREF_CARD_MODE_LARGE -> {
        decoration = SpacesItemDecoration(
          resources.getDimension(R.dimen.cardview_margin_parent_horizontal).toInt() / 2
        )
        adapter = BaseCardAdapter(LAYOUT_MODE_LARGE, lifecycleScope)
      }
      Const.PREF_CARD_MODE_MEDIUM -> {
        adapter = BaseCardAdapter(LAYOUT_MODE_MEDIUM, lifecycleScope)
      }
      Const.PREF_CARD_MODE_SMALL -> {
        adapter = BaseCardAdapter(LAYOUT_MODE_SMALL, lifecycleScope)
      }
      Const.PREF_CARD_MODE_MINIMUM -> {
        decoration = SpacesItemDecoration(
          resources.getDimension(R.dimen.cardview_margin_parent_horizontal).toInt() / 2
        )
        adapter = BaseCardAdapter(LAYOUT_MODE_MINIMUM, lifecycleScope)
      }
      else -> {
        adapter = BaseCardAdapter(LAYOUT_MODE_MEDIUM, lifecycleScope)
      }
    }

    adapter.apply {
      setDiffCallback(DiffListCallback())
      setOnItemClickListener { _, view, i ->
        clickItem(view, i)
      }
      setOnItemLongClickListener { _, view, i ->
        longClickItem(view, i)
      }
      setHasStableIds(true)
    }

    with(binding.recyclerView) {
      adapter = this@CategoryCardFragment.adapter
      setRecyclerViewLayoutManager(this, resources.configuration)
      addItemDecoration(decoration)
      paddingStartCompat = decoration.space
      paddingEndCompat = decoration.space
      addSystemBarPaddingAsync(addStatusBarPadding = false)
    }

    itemTouchHelper = ItemTouchHelper(ItemTouchCallBack().apply {
      setOnItemTouchListener(adapter)
    }).apply {
      attachToRecyclerView(null)
    }
  }

  private fun initObservers() {
    GlobalValues.cardModeLiveData.observe(viewLifecycleOwner, cardObserver)
    observeEntitiesList()
  }

  private fun unregisterObservers() {
    GlobalValues.cardModeLiveData.removeObserver(cardObserver)
    AnywhereApplication.sRepository.allAnywhereEntities.removeObserver(listObserver)
  }

  private fun updateItems(list: List<AnywhereEntity>) {
    adapter.setDiffNewData(
      if (GlobalValues.isPages) {
        if (category == AnywhereType.Category.DEFAULT_CATEGORY) {
          list.filter { it.category.isNullOrEmpty() || it.category == this.category }
            .toMutableList()
        } else {
          list.filter { it.category == this.category }.toMutableList()
        }
      } else {
        list.toMutableList()
      }
    )
    updateWidget(Utils.getApp())
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
          list.filter { it.category.isNullOrEmpty() || it.category == this.category }
            .toMutableList()
        } else {
          list.filter { it.category == this.category }.toMutableList()
        }
      )
    }
  }

  private fun setRecyclerViewLayoutManager(
    recyclerView: RecyclerView,
    configuration: Configuration
  ) {
    recyclerView.layoutManager =
      if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        val spanCount = when (GlobalValues.cardMode) {
          Const.PREF_CARD_MODE_LARGE -> 2
          Const.PREF_CARD_MODE_MEDIUM, Const.PREF_CARD_MODE_SMALL -> 4
          Const.PREF_CARD_MODE_MINIMUM -> 8
          else -> 4
        }
        WrapContentStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
      } else {
        val spanCount = when (GlobalValues.cardMode) {
          Const.PREF_CARD_MODE_LARGE -> 1
          Const.PREF_CARD_MODE_MEDIUM, Const.PREF_CARD_MODE_SMALL -> 2
          Const.PREF_CARD_MODE_MINIMUM -> 4
          else -> 2
        }
        WrapContentStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
      }
  }

  private fun observeEntitiesList() {
    AnywhereApplication.sRepository.allAnywhereEntities.removeObserver(listObserver)
    AnywhereApplication.sRepository.allAnywhereEntities.observe(viewLifecycleOwner, listObserver)
  }

  companion object {
    fun newInstance(category: String): CategoryCardFragment {
      return CategoryCardFragment().apply {
        arguments = Bundle().apply {
          putString(BUNDLE_CATEGORY, category)
        }
      }
    }

    var currentReference: WeakReference<CategoryCardFragment>? = null
  }
}
