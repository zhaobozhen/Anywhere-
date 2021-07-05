package com.absinthe.anywhere_.ui.cloud

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.adapter.cloud.CloudRulesAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.api.ApiManager
import com.absinthe.anywhere_.api.GitHubApi
import com.absinthe.anywhere_.databinding.ActivityCloudRulesBinding
import com.absinthe.anywhere_.model.cloud.GiteeApiContentBean
import com.absinthe.anywhere_.utils.manager.DialogManager
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rikka.widget.borderview.BorderView
import timber.log.Timber

class CloudRulesActivity : AppBarActivity<ActivityCloudRulesBinding>() {

    private val mAdapter = CloudRulesAdapter()

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
            addItemDecoration(DividerItemDecoration(this@CloudRulesActivity, DividerItemDecoration.VERTICAL))
            FastScrollerBuilder(this).useMd2Style().build()
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            DialogManager.showCloudRuleDialog(this@CloudRulesActivity, mAdapter.data[position].download_url!!)
        }
    }

    private fun requestRules() {
        binding.progressHorizontal.show()
        val retrofit = Retrofit.Builder()
                .baseUrl(ApiManager.GITEE_RULES_REPO)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val request = retrofit.create(GitHubApi::class.java)
        val task = request.requestGiteeAllContents()
        task.enqueue(object : Callback<List<GiteeApiContentBean>> {
            override fun onResponse(call: Call<List<GiteeApiContentBean>>, response: Response<List<GiteeApiContentBean>>) {
                val list = response.body()
                list?.let {
                    mAdapter.setList(it.filter { content -> content.type == "file" })
                }
                binding.progressHorizontal.hide()
            }

            override fun onFailure(call: Call<List<GiteeApiContentBean>>, t: Throwable) {
                Timber.e(t)
                binding.progressHorizontal.hide()
            }
        })
    }
}