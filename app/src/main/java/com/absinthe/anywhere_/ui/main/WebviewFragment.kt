package com.absinthe.anywhere_.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.FragmentWebviewBinding
import com.absinthe.anywhere_.ui.settings.SettingsActivity
import com.absinthe.anywhere_.utils.manager.URLManager

class WebviewFragment : Fragment() {

    private lateinit var mBinding: FragmentWebviewBinding
    private lateinit var mUri: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentWebviewBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUri = arguments?.getString(BUNDLE_URI) ?: URLManager.DOCUMENT_PAGE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        setHasOptionsMenu(true)
        mBinding.wvContainer.apply {
            setBackgroundColor(Color.TRANSPARENT)

            settings.apply {
                javaScriptEnabled = true
                allowFileAccess = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
            }

            loadUrl(mUri)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.web_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_settings) {
            startActivity(Intent(context, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val BUNDLE_URI = "BUNDLE_URI"

        fun newInstance(uri: String?): WebviewFragment {
            return WebviewFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_URI, uri)
                }
            }
        }
    }
}