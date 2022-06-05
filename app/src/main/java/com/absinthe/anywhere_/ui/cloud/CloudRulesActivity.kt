package com.absinthe.anywhere_.ui.cloud

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.cloud.CloudRulesAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.api.ApiManager
import com.absinthe.anywhere_.api.RuleApi
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityCloudRulesBinding
import com.absinthe.anywhere_.model.cloud.RuleEntity
import com.absinthe.anywhere_.utils.manager.DialogManager
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rikka.widget.borderview.BorderView
import timber.log.Timber

class CloudRulesActivity : AppBarActivity<ActivityCloudRulesBinding>(),
  SearchView.OnQueryTextListener {

  private val mAdapter = CloudRulesAdapter()
  private var mList = listOf<RuleEntity>()
  private var isListReady = false

  override fun setViewBinding() = ActivityCloudRulesBinding.inflate(layoutInflater)

  override fun getToolBar() = binding.toolbar.toolBar

  override fun getAppBarLayout() = binding.toolbar.appBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestRules()
  }

  override fun initView() {
    binding.list.apply {
      layoutManager = WrapContentLinearLayoutManager(this@CloudRulesActivity)
      adapter = mAdapter
      borderVisibilityChangedListener =
        BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
          appBar?.setRaised(!top)
        }
      addItemDecoration(
        DividerItemDecoration(
          this@CloudRulesActivity,
          DividerItemDecoration.VERTICAL
        )
      )
      FastScrollerBuilder(this).useMd2Style().build()
    }
    mAdapter.apply {
      setOnItemClickListener { _, _, position ->
        DialogManager.showCloudRuleDialog(this@CloudRulesActivity, mAdapter.data[position])
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.cloud_rules_menu, menu)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    val searchView = menu.findItem(R.id.search).actionView as SearchView
    val showSystemApp = menu.findItem(R.id.show_system_app)
    searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_bar)?.layoutTransition =
      LayoutTransition()

    searchView.apply {
      isQueryRefinementEnabled = true
      setIconifiedByDefault(false)
      setSearchableInfo(searchManager.getSearchableInfo(componentName))
      setOnQueryTextListener(this@CloudRulesActivity)
    }

    // Bug of DayNight lib
    showSystemApp?.apply {
      setTitle(
        if (GlobalValues.showSystemApps) {
          R.string.menu_hide_system_app
        } else {
          R.string.menu_show_system_app
        }
      )
    }

    if (!isListReady) {
      menu.findItem(R.id.search).isVisible = false
    }

    return true
  }

  override fun onQueryTextSubmit(query: String): Boolean {
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean {
    val filter = mList.filter {
      it.name.contains(newText, ignoreCase = true)
    }
    mAdapter.setDiffNewData(filter.toMutableList())
    return false
  }

  private fun requestRules() {
    binding.progressHorizontal.show()
    val retrofit = Retrofit.Builder()
      .baseUrl(ApiManager.GITLAB_RULES_RAW_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    val request = retrofit.create(RuleApi::class.java)
    val task = request.requestAllRules()
    task.enqueue(object : Callback<List<RuleEntity>> {
      override fun onResponse(call: Call<List<RuleEntity>>, response: Response<List<RuleEntity>>) {
        val list = response.body()
        list?.let {
          mAdapter.setList(list)
          mList = list
        }
        getToolBar().menu?.findItem(R.id.search)?.isVisible = true
        binding.progressHorizontal.hide()
        isListReady = true
      }

      override fun onFailure(call: Call<List<RuleEntity>>, t: Throwable) {
        Timber.e(t)
        binding.progressHorizontal.hide()
      }
    })
  }
}
