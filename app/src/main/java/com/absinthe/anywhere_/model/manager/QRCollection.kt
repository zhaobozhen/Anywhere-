package com.absinthe.anywhere_.model.manager

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.absinthe.anywhere_.a11y.A11yEntity
import com.absinthe.anywhere_.a11y.A11yType
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.listener.OnQRLaunchedListener
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.QREntity
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
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
    fun checkAccessibilityEnabled(): Boolean {
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
                    Opener.with(getContext()).load(cmd).openWithPackageName(pkgName)
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
                    val a11yEntity = A11yEntity().apply {
                        applicationId = pkgName
                        entryActivity = clsName
                        actions = listOf(
                                A11yActionBean(A11yType.TEXT, "我|Me", "", 300L),
                                A11yActionBean(A11yType.TEXT, "支付|WeChat Pay", "", 800L),
                                A11yActionBean(A11yType.TEXT, "收付款|Money", "", 0L),
                        )
                    }
                    val entity = AnywhereEntity.Builder().apply {
                        type = AnywhereType.Card.ACCESSIBILITY
                        param1 = Gson().toJson(a11yEntity)
                    }
                    Opener.with(getContext()).load(entity).open()
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
                    Opener.with(getContext()).load(cmd).openWithPackageName(pkgName)
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
                    val a11yEntity = A11yEntity().apply {
                        applicationId = pkgName
                        entryActivity = clsName
                        actions = listOf(
                                A11yActionBean(A11yType.TEXT, "我|Me", "", 300L),
                                A11yActionBean(A11yType.TEXT, "支付|WeChat Pay", "", 800L),
                                A11yActionBean(A11yType.TEXT, "收付款|Money", "", 800L),
                                A11yActionBean(A11yType.TEXT, "二维码收款|Receive Money", "", 0L, "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI"),
                        )
                    }
                    val entity = AnywhereEntity.Builder().apply {
                        type = AnywhereType.Card.ACCESSIBILITY
                        param1 = Gson().toJson(a11yEntity)
                    }
                    Opener.with(getContext()).load(entity).open()
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
                        URLSchemeHandler.parse(getContext(), urlScheme, pkgName)
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
                        URLSchemeHandler.parse(getContext(), urlScheme, pkgName)
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
                        URLSchemeHandler.parse(getContext(), urlScheme, pkgName)
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
                        URLSchemeHandler.parse(getContext(), urlScheme, pkgName)
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
        val clsName = "com.unionpay.activity.UPActivityWelcome"
        list.add(AnywhereEntity.Builder().apply {
            this.id = id
            appName = "云闪付${text}"
            param1 = pkgName
            description = getContext().getString(R.string.desc_work_at_any_mode)
            type = AnywhereType.Card.QR_CODE
        })
        val extraData = when (text) {
            "乘车码" -> "upwallet://rn/rnhtmlridingcode"
            "付款码" -> "upwallet://native/codepay"
            "收款码" -> "upwallet://native/codecollect"
            "扫一扫" -> "upwallet://native/scanCode"
            else -> ""
        }
        return QREntity(object : OnQRLaunchedListener {
            override fun onLaunched() {
                val extraBean = ExtraBean(
                        data = extraData,
                        action = Intent.ACTION_VIEW,
                        extras = emptyList()
                )
                val entity = AnywhereEntity.Builder().apply {
                    type = AnywhereType.Card.ACTIVITY
                    param1 = pkgName
                    param2 = clsName
                    param3 = Gson().toJson(extraBean)
                }
                Opener.with(getContext()).load(entity).open()
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
     * UnionPay bus page
     */
    private val unionpayBus: QREntity
        get() = genUnionPay(unionpayBusId, "乘车码")

    /**
     * UnionPay scan page
     */
    private val unionpayScan: QREntity
        get() = genUnionPay(unionpayScanId, "扫一扫")

    /**
     * UnionPay collect page
     */
    private val unionpayCollect: QREntity
        get() {
            val pkgName = "com.unionpay"
            val clsName = "com.unionpay.activity.UPActivityMain"
            list.add(AnywhereEntity.Builder().apply {
                id = unionpayCollectId
                appName = "云闪付收款码"
                param1 = pkgName
                description = getContext().getString(R.string.desc_need_accessibility)
                type = AnywhereType.Card.QR_CODE
            })
            return QREntity(object : OnQRLaunchedListener {
                override fun onLaunched() {
                    val list = mutableListOf(
                            A11yActionBean(A11yType.TEXT, "知道了", "", 200L),
                            A11yActionBean(A11yType.TEXT, "跳过", "", 300L),
                            A11yActionBean(A11yType.TEXT, "收款码", "", 0L)
                    )

                    val a11yEntity = A11yEntity().apply {
                        applicationId = pkgName
                        entryActivity = clsName
                        actions = list
                    }
                    val entity = AnywhereEntity.Builder().apply {
                        type = AnywhereType.Card.ACCESSIBILITY
                        param1 = Gson().toJson(a11yEntity)
                    }
                    Opener.with(getContext()).load(entity).open()
                }
            }).apply {
                this.pkgName = pkgName
            }
        }

}