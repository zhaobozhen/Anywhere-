package com.absinthe.anywhere_.model.manager

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.listener.OnQRLaunchedListener
import com.absinthe.anywhere_.model.QREntity
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.services.IzukoService
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.workflow.FlowNode
import com.absinthe.anywhere_.workflow.WorkFlow
import com.blankj.utilcode.util.Utils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

object QRCollection {

    val list = mutableListOf<AnywhereEntity>()
    private val context = WeakReference<Context>(Utils.getApp())
    private val accessibilityManager: AccessibilityManager
    private val map: HashMap<String, QREntity>

    private const val wechatScanId = "wechatScan"
    private const val wechatPayId = "wechatPay"
    private const val wechatPayAcsId = "wechatPayAcs"
    private const val wechatCollectId = "wechatCollect"
    private const val wechatCollectAcsId = "wechatCollectAcs"
    private const val alipayScanId = "alipayScan"
    private const val alipayPayId = "alipayPay"
    private const val alipayBusId = "alipayBus"
    private const val alipayCollectId = "alipayCollect"
    private const val qqScanId = "qqScan"
    private const val unionpayPayId = "unionpayPay"
    private const val unionpayCollectId = "unionpayCollect"
    private const val unionpayScanId = "unionpayScan"
    private const val unionpayBusId = "unionpayBus"

    init {
        accessibilityManager = getContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        map = HashMap()
        map[wechatScanId] = wechatScan
        map[wechatPayId] = wechatPay
        map[wechatPayAcsId] = wechatPayAcs
        map[wechatCollectId] = wechatCollect
        map[wechatCollectAcsId] = wechatCollectAcs
        map[alipayScanId] = alipayScan
        map[alipayPayId] = alipayPay
        map[alipayBusId] = alipayBus
        map[alipayCollectId] = alipayCollect
        map[qqScanId] = qqScan
        map[unionpayPayId] = unionpayPay
        map[unionpayCollectId] = unionpayCollect
        map[unionpayScanId] = unionpayScan
        map[unionpayBusId] = unionpayBus
    }

    fun getQREntity(id: String): QREntity? {
        return map[id]
    }

    private fun getContext(): Context {
        return context.get()!!
    }

    /**
     * Check 当前辅助服务是否启用
     *
     * @return 是否启用
     */
    private fun checkAccessibilityEnabled(): Boolean {
        val serviceName: String = if (BuildConfig.DEBUG) {
            "com.absinthe.anywhere_.debug/com.absinthe.anywhere_.services.IzukoService"
        } else {
            "com.absinthe.anywhere_/.services.IzukoService"
        }
        val accessibilityServices: List<AccessibilityServiceInfo> = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)

        for (info in accessibilityServices) {
            if (info.id == serviceName) {
                return true
            }
        }
        return false
    }

    /**
     * Wechat scan page
     */
    private val wechatScan: QREntity
        get() {
            val pkgName = "com.tencent.mm"
            list.add(AnywhereEntity.Builder().apply {
                id = wechatScanId
                appName = "微信扫码"
                param1 = pkgName
                description = getContext().getString(R.string.desc_work_at_any_mode)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    try {
                        val intent = getContext().packageManager.getLaunchIntentForPackage(pkgName)
                        if (intent != null) {
                            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            getContext().startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }).apply {
                this.pkgName = pkgName
            }
        }

    /**
     * Wechat pay page
     */
    private val wechatPay: QREntity
        get() {
            val pkgName = "com.tencent.mm"
            val clsName = ".plugin.offline.ui.WalletOfflineCoinPurseUI"
            val cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, pkgName + clsName)
            list.add(AnywhereEntity.Builder().apply {
                id = wechatPayId
                appName = "微信付款码"
                param1 = pkgName
                param2 = clsName
                description = getContext().getString(R.string.desc_need_root)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    Opener.with(getContext()).load(cmd).open()
                }
            }).apply {
                this.pkgName = pkgName
                this.clsName = clsName
            }
        }

    /**
     * Wechat pay page Accessibility
     */
    private val wechatPayAcs: QREntity
        get() {
            val pkgName = "com.tencent.mm"
            val clsName = "com.tencent.mm.ui.LauncherUI"
            list.add(AnywhereEntity.Builder().apply {
                id = wechatPayAcsId
                appName = "微信付款码"
                param1 = pkgName
                param2 = clsName
                description = getContext().getString(R.string.desc_need_accessibility)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    if (!checkAccessibilityEnabled() || IzukoService.getInstance() == null) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        getContext().startActivity(intent)
                        ToastUtil.makeText(R.string.toast_grant_accessibility)
                    } else {
                        IzukoService.getInstance()?.apply {
                            packageName = pkgName
                            setClassName(clsName)
                            isClicked(false)
                        }

                        val source = Observable.create { emitter: ObservableEmitter<FlowNode> ->
                            emitter.onNext(FlowNode("我", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("Me", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            Thread.sleep(300)

                            emitter.onNext(FlowNode("支付", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("WeChat Pay", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            Thread.sleep(800)

                            emitter.onNext(FlowNode("收付款", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("Money", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onComplete()
                        }
                        IzukoService.getInstance()?.setWorkFlow(WorkFlow().observe(source))

                        try {
                            getContext().packageManager.getLaunchIntentForPackage(pkgName)?.let {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                getContext().startActivity(it)
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            }).apply {
                this.pkgName = pkgName
                this.clsName = clsName
            }
        }

    /**
     * Wechat collect page
     */
    private val wechatCollect: QREntity
        get() {
            val pkgName = "com.tencent.mm"
            val clsName = ".plugin.collect.ui.CollectMainUI"
            val cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, pkgName + clsName)
            list.add(AnywhereEntity.Builder().apply {
                id = wechatCollectId
                appName = "微信收款码"
                param1 = pkgName
                param2 = clsName
                description = getContext().getString(R.string.desc_need_root)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    Opener.with(getContext()).load(cmd).open()
                }
            }).apply {
                this.pkgName = pkgName
                this.clsName = clsName
            }
        }

    /**
     * Wechat collect page Accessibility
     */
    private val wechatCollectAcs: QREntity
        get() {
            val pkgName = "com.tencent.mm"
            val clsName = "com.tencent.mm.ui.LauncherUI"
            list.add(AnywhereEntity.Builder().apply {
                id = wechatCollectAcsId
                appName = "微信收款码"
                param1 = pkgName
                param2 = clsName
                description = getContext().getString(R.string.desc_need_accessibility)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    if (!checkAccessibilityEnabled() || IzukoService.getInstance() == null) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        getContext().startActivity(intent)
                        ToastUtil.makeText(R.string.toast_grant_accessibility)
                    } else {
                        IzukoService.getInstance()?.apply {
                            packageName = pkgName
                            setClassName(clsName)
                            isClicked(false)
                        }

                        val source = Observable.create { emitter: ObservableEmitter<FlowNode> ->
                            emitter.onNext(FlowNode("我", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("Me", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            Thread.sleep(200)

                            emitter.onNext(FlowNode("支付", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("WeChat Pay", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            Thread.sleep(300)

                            emitter.onNext(FlowNode("收付款", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("Money", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            Thread.sleep(800)

                            IzukoService.getInstance()?.setClassName("com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI")
                            emitter.onNext(FlowNode("二维码收款", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onNext(FlowNode("Receive Money", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                            emitter.onComplete()
                        }
                        IzukoService.getInstance()?.setWorkFlow(WorkFlow().observe(source))

                        try {
                            getContext().packageManager.getLaunchIntentForPackage(pkgName)?.let {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                getContext().startActivity(it)
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            }).apply {
                this.pkgName = pkgName
                this.clsName = clsName
            }
        }

    /**
     * Alipay scan page
     */
    private val alipayScan: QREntity
        get() {
            val urlScheme = "alipayqr://platformapi/startapp?saId=10000007"
            val pkgName = "com.eg.android.AlipayGphone"
            list.add(AnywhereEntity.Builder().apply {
                id = alipayScanId
                appName = "支付宝扫码"
                param1 = pkgName
                param3 = urlScheme
                description = getContext().getString(R.string.desc_work_at_any_mode)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    try {
                        URLSchemeHandler.parse(getContext(), urlScheme)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }).apply {
                this.urlScheme = urlScheme
            }
        }

    /**
     * Alipay pay page
     */
    private val alipayPay: QREntity
        get() {
            val urlScheme = "alipays://platformapi/startapp?appId=20000056"
            val pkgName = "com.eg.android.AlipayGphone"
            list.add(AnywhereEntity.Builder().apply {
                id = alipayPayId
                appName = "支付宝付款码"
                param1 = pkgName
                param3 = urlScheme
                description = getContext().getString(R.string.desc_work_at_any_mode)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    try {
                        URLSchemeHandler.parse(getContext(), urlScheme)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }).apply {
                this.urlScheme = urlScheme
            }
        }


    /**
     * Alipay bus page
     */
    private val alipayBus: QREntity
        get() {
            val urlScheme = "alipayqr://platformapi/startapp?saId=200011235"
            val pkgName = "com.eg.android.AlipayGphone"
            list.add(AnywhereEntity.Builder().apply {
                id = alipayBusId
                appName = "支付宝公交码"
                param1 = pkgName
                param3 = urlScheme
                description = getContext().getString(R.string.desc_work_at_any_mode)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    try {
                        URLSchemeHandler.parse(getContext(), urlScheme)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }).apply {
                this.urlScheme = urlScheme
            }
        }

    /**
     * Alipay collect page
     */
    private val alipayCollect: QREntity
        get() {
            val urlScheme = "alipays://platformapi/startapp?appId=20000123"
            val pkgName = "com.eg.android.AlipayGphone"
            list.add(AnywhereEntity.Builder().apply {
                id = alipayCollectId
                appName = "支付宝收款码"
                param1 = pkgName
                param3 = urlScheme
                description = getContext().getString(R.string.desc_work_at_any_mode)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    try {
                        URLSchemeHandler.parse(getContext(), urlScheme)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }).apply {
                this.urlScheme = urlScheme
            }
        }

    /**
     * QQ scan page
     */
    private val qqScan: QREntity
        get() {
            val pkgName = "com.tencent.mobileqq"
            val clsName = "com.tencent.biz.qrcode.activity.ScannerActivity"
            val cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, clsName)
            list.add(AnywhereEntity.Builder().apply {
                id = qqScanId
                appName = "QQ 扫码"
                param1 = pkgName
                param2 = clsName
                description = getContext().getString(R.string.desc_need_root)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    Opener.with(getContext()).load(cmd).open()
                }
            }).apply {
                this.pkgName = pkgName
                this.clsName = clsName
            }
        }

    private fun genUnionPay(id: String, text: String): QREntity {
        val pkgName = "com.unionpay"
        val clsName = "com.unionpay.activity.UPActivityMain"
        list.add(AnywhereEntity.Builder().apply {
            this.id = id
            appName = "云闪付" + text.split("&".toRegex()).toTypedArray()[0]
            param1 = pkgName
            description = getContext().getString(R.string.desc_need_accessibility)
            type = AnywhereType.Card.QR_CODE
        })
        return QREntity(object : OnQRLaunchedListener {
            override fun onLaunched() {
                if (!checkAccessibilityEnabled() || IzukoService.getInstance() == null) {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    getContext().startActivity(intent)
                    ToastUtil.makeText(R.string.toast_grant_accessibility)
                } else {
                    IzukoService.getInstance()?.apply {
                        packageName = pkgName
                        setClassName(clsName)
                        isClicked(false)
                    }

                    val source = Observable.create { emitter: ObservableEmitter<FlowNode> ->
                        emitter.onNext(FlowNode("知道了", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                        Thread.sleep(200)

                        emitter.onNext(FlowNode("跳过", FlowNode.TYPE_ACCESSIBILITY_TEXT))
                        Thread.sleep(200)

                        for (split in text.split("&".toRegex()).toTypedArray()) {
                            emitter.onNext(FlowNode(split, FlowNode.TYPE_ACCESSIBILITY_TEXT))
                        }
                        emitter.onComplete()
                    }
                    IzukoService.getInstance()?.setWorkFlow(WorkFlow().observe(source))

                    try {
                        getContext().packageManager.getLaunchIntentForPackage(pkgName)?.let {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            getContext().startActivity(it)
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }).apply {
            this.pkgName = pkgName
        }
    }

    /**
     * UnionPay pay page
     */
    private val unionpayPay: QREntity
        get() = genUnionPay(unionpayPayId, "付款码")

    /**
     * UnionPay collect page
     */
    private val unionpayCollect: QREntity
        get() = genUnionPay(unionpayCollectId, "收款码")


    /**
     * UnionPay scan page
     */
    private val unionpayScan: QREntity
        get() = genUnionPay(unionpayScanId, "扫一扫")

    /**
     * UnionPay bus page
     */
    private const val unionPayBusConstants = "乘车码&坐公交"
    private val unionpayBus: QREntity
        get() = genUnionPay(unionpayBusId, unionPayBusConstants)

}