package com.absinthe.anywhere_.ui.about

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager.showDebugDialog
import com.absinthe.anywhere_.utils.manager.URLManager
import com.blankj.utilcode.util.AppUtils
import com.drakeet.about.*
import com.drakeet.about.extension.RecommendationLoaderDelegate
import com.drakeet.about.extension.provided.GsonJsonConverter
import com.drakeet.about.provided.GlideImageLoader
import com.google.android.material.appbar.AppBarLayout
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
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
        icon.apply {
            setImageResource(R.drawable.pic_splash)
            setOnClickListener(createDebugListener())
        }

        slogan.text = getString(R.string.slogan)
        version.text = String.format("Version: %s", BuildConfig.VERSION_NAME)
    }

    override fun onItemsCreated(items: MutableList<Any>) {

        val hasInstallCoolApk = AppUtils.isAppInstalled("com.coolapk.market")

        items.apply {
            add(Category(getString(R.string.whats_this)))
            add(Card(getString(R.string.about_text)))

            add(Category(getString(R.string.developer)))
            val developerUrl = if (hasInstallCoolApk) { URLManager.COOLAPK_PAGE } else { URLManager.GITHUB_PAGE }
            add(Contributor(R.mipmap.pic_rabbit, "Absinthe", getString(R.string.developer_info), developerUrl))

            add(Category(getString(R.string.certification)))
            add(Contributor(R.mipmap.pic_android_links, getString(R.string.android_links_title), "https://androidlinks.org/", "https://androidlinks.org/"))
            add(Contributor(R.drawable.ic_green_android, getString(R.string.green_android_title), "https://green-android.org/", "https://green-android.org/"))

            add(Category(getString(R.string.other_works)))
            add(Contributor(R.mipmap.libchecker_icon, "LibChecker", getString(R.string.lc_intro), URLManager.MARKET_DETAIL_SCHEME + "com.absinthe.libchecker"))
            add(Contributor(R.mipmap.kage_icon, "Kage(Beta)", getString(R.string.kage_intro), URLManager.MARKET_DETAIL_SCHEME + "com.absinthe.kage"))

            add(Category(getString(R.string.communication)))
            add(Card(
                    HtmlCompat.fromHtml("Telegram: <a href=\"t.me/anywhereee\">t.me/anywhereee</a><br>E-mail: zhaobozhen2025@gmail.com", HtmlCompat.FROM_HTML_MODE_LEGACY)
            ))

            add(Category(getString(R.string.open_source_licenses)))
            add(License("Kotlin", "JetBrains", License.APACHE_2, "https://github.com/JetBrains/kotlin"))
            add(License("Shizuku", "Rikka", License.APACHE_2, "https://github.com/RikkaApps/Shizuku"))
            add(License("FreeReflection", "tiann", License.MIT, "https://github.com/tiann/FreeReflection"))
            add(License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"))
            add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"))
            add(License("glide", "bumptech", "BSD, part MIT and Apache 2.0", "https://github.com/bumptech/glide"))
            add(License("AndResGuard", "shwenzhang", License.APACHE_2, "https://github.com/shwenzhang/AndResGuard"))
            add(License("Delegated-Scopes-Manager", "heruoxin", "WTFPL", "https://github.com/heruoxin/Delegated-Scopes-Manager"))
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
            add(License("contour", "Square", License.APACHE_2, "https://github.com/cashapp/contour"))
            add(License("timber", "JakeWharton", License.APACHE_2, "https://github.com/JakeWharton/timber"))
            add(License("RxAndroid", "JakeWharton", License.APACHE_2, "https://github.com/ReactiveX/RxAndroid"))
            add(License("RxJava", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxJava"))
            add(License("android-target-tooltip", "sephiroth74", License.MIT, "https://github.com/sephiroth74/android-target-tooltip"))
            add(License("AndroidUtilCode", "Blankj", License.APACHE_2, "https://github.com/Blankj/AndroidUtilCode"))
            add(License("MMKV", "Tencent", "BSD 3-Clause License", "https://github.com/Tencent/MMKV"))

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
                if (GlobalValues.sIsDebugMode) {
                    try {
                        val inputStream = assets.open("renge.webp")
                        findViewById<ImageView>(com.drakeet.about.R.id.icon).setImageBitmap(BitmapFactory.decodeStream(inputStream))
                        findViewById<TextView>(com.drakeet.about.R.id.slogan).text = "えい、私もよ。"
                        setHeaderBackground(ColorDrawable(ContextCompat.getColor(this, R.color.renge)))
                        setHeaderContentScrim(ColorDrawable(ContextCompat.getColor(this, R.color.renge)))
                        window.statusBarColor = ContextCompat.getColor(this, R.color.renge)

                        val fd = assets.openFd("renge_no_koe.aac")
                        MediaPlayer().apply {
                            setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                            prepare()
                            start()
                        }
                    } catch (e: Exception) { }
                } else {
                    GlobalValues.sIsDebugMode = true
                    showDebugDialog(this)
                }
            }
        }
    }

    override fun onRecommendationClicked(itemView: View, recommendation: Recommendation): Boolean {
        return false
    }

    private fun initView() {
        StatusBarUtil.setSystemBarStyle(this, false)

        val appbar = findViewById<AppBarLayout>(com.drakeet.about.R.id.header_layout)
        appbar.fitsSystemWindows = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.toolbar_rate) {
            try {
                URLSchemeHandler.parse(this, URLManager.ANYWHERE_MARKET_URL)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        } else if (menuItem.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}