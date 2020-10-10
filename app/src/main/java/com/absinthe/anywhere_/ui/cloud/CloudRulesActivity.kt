package com.absinthe.anywhere_.ui.cloud

import android.os.Bundle
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.adapter.cloud.CloudRulesAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.api.ApiManager
import com.absinthe.anywhere_.api.GitHubApi
import com.absinthe.anywhere_.databinding.ActivityCloudRulesBinding
import com.absinthe.anywhere_.model.cloud.GitHubApiContentBean
import com.absinthe.anywhere_.model.cloud.RuleEntity
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.google.gson.Gson
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
            addPaddingBottom(StatusBarUtil.getNavBarHeight())
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            Timber.d("onClick")
            val retrofit = Retrofit.Builder()
                    .baseUrl(ApiManager.RULES_RAW_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val request = retrofit.create(GitHubApi::class.java)
            val task = request.requestEntity(mAdapter.data[position].download_url!!.removePrefix(ApiManager.RULES_RAW_URL))
            task.enqueue(object : Callback<RuleEntity> {
                override fun onResponse(call: Call<RuleEntity>, response: Response<RuleEntity>) {
                    val entity = response.body()
                    entity?.let {
                        val decrypted = CipherUtils.decrypt(it.content)
                        val ae = Gson().fromJson(decrypted, AnywhereEntity::class.java)
                        DialogManager.showCloudRuleDialog(this@CloudRulesActivity, ae)
                    }
                }

                override fun onFailure(call: Call<RuleEntity>, t: Throwable) {
                    Timber.e(t)
                }
            })
        }
    }

    private fun requestRules() {
        val retrofit = Retrofit.Builder()
                .baseUrl(ApiManager.RULES_REPO)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val request = retrofit.create(GitHubApi::class.java)
        val task = request.requestAllContents()
        task.enqueue(object : Callback<List<GitHubApiContentBean>> {
            override fun onResponse(call: Call<List<GitHubApiContentBean>>, response: Response<List<GitHubApiContentBean>>) {
                val list = response.body()
                list?.let {
                    mAdapter.setList(it)
                }
            }

            override fun onFailure(call: Call<List<GitHubApiContentBean>>, t: Throwable) {
                Timber.e(t)
            }
        })
    }
}