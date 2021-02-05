package com.absinthe.anywhere_.ui.cloud

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.adapter.cloud.CloudRulesAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.api.ApiManager
import com.absinthe.anywhere_.api.GitHubApi
import com.absinthe.anywhere_.databinding.ActivityCloudRulesBinding
import com.absinthe.anywhere_.model.cloud.GiteeApiContentBean
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.absinthe.libraries.utils.utils.UiUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class CloudRulesActivity : BaseActivity() {

    private lateinit var binding: ActivityCloudRulesBinding
    private val mAdapter = CloudRulesAdapter()

    override fun setViewBinding() {
        isPaddingToolbar = true
        binding = ActivityCloudRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.toolbar.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestRules()
    }

    override fun initView() {
        super.initView()

        binding.rvAppList.apply {
            layoutManager = WrapContentLinearLayoutManager(this@CloudRulesActivity)
            adapter = mAdapter
            addPaddingBottom(UiUtils.getNavBarHeight(windowManager))
            addItemDecoration(DividerItemDecoration(this@CloudRulesActivity, DividerItemDecoration.VERTICAL))
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            DialogManager.showCloudRuleDialog(this@CloudRulesActivity, mAdapter.data[position].download_url!!)
        }
        binding.srlAppList.apply {
            isRefreshing = true
            setOnRefreshListener {
                requestRules()
            }
        }
    }

    private fun requestRules() {
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
                binding.srlAppList.isRefreshing = false
            }

            override fun onFailure(call: Call<List<GiteeApiContentBean>>, t: Throwable) {
                Timber.e(t)
                binding.srlAppList.isRefreshing = false
            }
        })
    }
}