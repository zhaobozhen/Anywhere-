package com.absinthe.anywhere_.ui.about

import android.content.ActivityNotFoundException
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager.showDebugDialog
import com.absinthe.anywhere_.utils.manager.URLManager
import com.drakeet.about.*
import com.drakeet.about.extension.RecommendationLoaderDelegate
import com.drakeet.about.extension.provided.GsonJsonConverter
import com.drakeet.about.provided.GlideImageLoader

class AboutActivity : AbsAboutActivity(), OnRecommendationClickedListener {

    private var mClickCount = 0
    private var mStartTime: Long = 0
    private var mEndTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setImageLoader(GlideImageLoader())
        onRecommendationClickedListener = this
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        val listener = createDebugListener()
        icon.setImageResource(R.drawable.pic_splash)
        icon.setOnClickListener(listener)
        slogan.text = getString(R.string.slogan)
        version.text = String.format("Version: %s", BuildConfig.VERSION_NAME)
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            add(Category(getString(R.string.whats_this)))
            add(Card(getString(R.string.about_text)))

            add(Category(getString(R.string.developer)))
            add(Contributor(R.mipmap.pic_rabbit, "Absinthe", getString(R.string.developer_info), "https://www.coolapk.com/u/482045"))

            add(Category(getString(R.string.certification)))
            add(Contributor(R.mipmap.pic_android_links, getString(R.string.android_links_title), "https://androidlinks.org/", "https://androidlinks.org/"))
            add(Contributor(R.drawable.ic_green_android, getString(R.string.green_android_title), "https://green-android.org/", "https://green-android.org/"))

            add(Category(getString(R.string.other_works)))
            add(Contributor(R.mipmap.kage_icon, "Kage(Beta)", getString(R.string.kage_intro), "https://www.coolapk.com/apk/com.absinthe.kage"))

            add(Category(getString(R.string.open_source_licenses)))
            add(License("Kotlin", "JetBrains", License.APACHE_2, "https://github.com/JetBrains/kotlin"))
            add(License("Shizuku", "Rikka", License.APACHE_2, "https://github.com/RikkaApps/Shizuku"))
            add(License("FreeReflection", "tiann", License.MIT, "https://github.com/tiann/FreeReflection"))
            add(License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"))
            add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"))
            add(License("glide", "bumptech", "BSD, part MIT and Apache 2.0", "https://github.com/bumptech/glide"))
            add(License("AndResGuard", "shwenzhang", License.APACHE_2, "https://github.com/shwenzhang/AndResGuard"))
            add(License("IceBox-SDK", "heruoxin", License.APACHE_2, "https://github.com/heruoxin/IceBox-SDK"))
            add(License("Robfuscate", "heruoxin", License.APACHE_2, "https://github.com/heruoxin/Robfuscate"))
            add(License("Once", "jonfinerty", License.APACHE_2, "https://github.com/jonfinerty/Once"))
            add(License("BaseRecyclerViewAdapterHelper", "CymChad", License.MIT, "https://github.com/CymChad/BaseRecyclerViewAdapterHelper"))
            add(License("FloatingActionButtonSpeedDial", "leinardi", License.APACHE_2, "https://github.com/leinardi/FloatingActionButtonSpeedDial"))
            add(License("colorpicker", "QuadFlask", License.APACHE_2, "https://github.com/QuadFlask/colorpicker"))
            add(License("gson", "Google", License.APACHE_2, "https://github.com/google/gson"))
            add(License("zxing", "zxing", License.APACHE_2, "https://github.com/zxing/zxing"))
            add(License("AndroidX", "Google", License.APACHE_2, "https://source.google.com"))
            add(License("Android Jetpack", "Google", License.APACHE_2, "https://source.google.com"))
            add(License("Palette", "Google", License.APACHE_2, "https://source.google.com"))
            add(License("OkHttp", "Square", License.APACHE_2, "https://github.com/square/okhttp"))
            add(License("Retrofit", "Square", License.APACHE_2, "https://github.com/square/retrofit"))
            add(License("LeakCanary", "Square", License.APACHE_2, "https://github.com/square/leakcanary"))
            add(License("timber", "JakeWharton", License.APACHE_2, "https://github.com/JakeWharton/timber"))
            add(License("RxAndroid", "JakeWharton", License.APACHE_2, "https://github.com/ReactiveX/RxAndroid"))
            add(License("RxJava", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxJava"))
            add(License("android-target-tooltip", "sephiroth74", License.MIT, "https://github.com/sephiroth74/android-target-tooltip"))
            add(License("AndroidUtilCode", "Blankj", License.APACHE_2, "https://github.com/Blankj/AndroidUtilCode"))
        }

        RecommendationLoaderDelegate.attach(this, items.size, GsonJsonConverter())
    }

    private fun createDebugListener(): View.OnClickListener {
        mClickCount = 0
        mEndTime = 0
        mStartTime = mEndTime

        return View.OnClickListener {
            mEndTime = System.currentTimeMillis()

            if (mEndTime - mStartTime > 500) {
                mClickCount = 0
            } else {
                mClickCount++
            }

            mStartTime = mEndTime

            if (mClickCount == 9) {
                GlobalValues.sIsDebugMode = true
                showDebugDialog(this)
            }
        }
    }

    override fun onRecommendationClicked(itemView: View, recommendation: Recommendation): Boolean {
        return false
    }

    private fun initView() {
        window.navigationBarColor = Color.TRANSPARENT
        if (!UiUtils.isDarkMode(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.toolbar_rate) {
            try {
                URLSchemeHandler.parse(URLManager.MARKET_URL_SCHEME, this)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}