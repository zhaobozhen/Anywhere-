package com.absinthe.anywhere_.ui.cloud

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.adapter.cloud.CloudRulesAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.api.GitHubApi
import com.absinthe.anywhere_.databinding.ActivityCloudRulesBinding
import com.absinthe.anywhere_.model.cloud.GitHubApiContentBean
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.libraries.utils.extensions.addPaddingBottom
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

        binding.rvAppList.apply {
            layoutManager = WrapContentLinearLayoutManager(this@CloudRulesActivity)
            adapter = mAdapter
            addPaddingBottom(StatusBarUtil.getNavBarHeight())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (!recyclerView.canScrollVertically(-1) && !binding.extendedFab.isExtended) {
                            binding.extendedFab.extend()
                        } else {
                            binding.extendedFab.shrink()
                        }
                    }
                }
            })
        }

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/repos/zhaobozhen/Anywhere-Docs/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val request = retrofit.create(GitHubApi::class.java)
        val task = request.requestAllContents()
        task.enqueue(object : Callback<List<GitHubApiContentBean>> {
            override fun onResponse(call: Call<List<GitHubApiContentBean>>, response: Response<List<GitHubApiContentBean>>) {
                Timber.d(response.body().toString())
                response.body()?.let {
                    mAdapter.data = it.toMutableList()
                }
            }

            override fun onFailure(call: Call<List<GitHubApiContentBean>>, t: Throwable) {
                Timber.e(t)
            }
        })
    }
}