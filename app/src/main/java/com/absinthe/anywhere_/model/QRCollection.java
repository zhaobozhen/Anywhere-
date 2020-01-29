package com.absinthe.anywhere_.model;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.IzukoService;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.Logger;

import java.util.ArrayList;
import java.util.List;

public class QRCollection {

    public enum Singleton {
        INSTANCE;
        private QRCollection instance;

        Singleton() {
            instance = new QRCollection();
        }

        public QRCollection getInstance() {
            return instance;
        }
    }

    private Context mContext;
    private AccessibilityManager mAccessibilityManager;
    private ArrayList<AnywhereEntity> mList;

    private QRCollection() {
        mContext = AnywhereApplication.sContext;
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);

        mList = new ArrayList<>();
        mList.add(genWechatScan());
        mList.add(genWechatPay());
        mList.add(genWechatCollect());
        mList.add(genAlipayScan());
        mList.add(genAlipayPay());
        mList.add(genAlipayBus());
        mList.add(genAlipayCollect());
        mList.add(genQqScan());
        mList.add(genUnionpayPay());
        mList.add(genUnionpayCollect());
        mList.add(genUnionpayScan());
//        mList.add(genUnionpaySignIn());
    }

    public ArrayList<AnywhereEntity> getList() {
        return mList;
    }

    public QREntity getQREntity(String id) {
        switch (id) {
            case wechatScanId:
                return wechatScan;
            case wechatPayId:
                return wechatPay;
            case wechatCollectId:
                return wechatCollect;
            case alipayScanId:
                return alipayScan;
            case alipayPayId:
                return alipayPay;
            case alipayBusId:
                return alipayBus;
            case alipayCollectId:
                return alipayCollect;
            case qqScanId:
                return qqScan;
            case unionpayPayId:
                return unionpayPay;
            case unionpayCollectId:
                return unionpayCollect;
            case unionpayScanId:
                return unionpayScan;
            case unionpaySignInId:
                return unionpaySignIn;
            default:
                return null;
        }
    }

    /**
     * Check 当前辅助服务是否启用
     *
     * @return 是否启用
     */
    private boolean checkAccessibilityEnabled() {
        String serviceName;

        if (BuildConfig.DEBUG) {
            serviceName = "com.absinthe.anywhere_.debug/com.absinthe.anywhere_.services.IzukoService";
        } else {
            serviceName = "com.absinthe.anywhere_/.services.IzukoService";
        }

        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            Logger.d(info.getId());
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wechat scan page
     */
    public static final String wechatScanId = "wechatScan";
    public QREntity wechatScan;

    private AnywhereEntity genWechatScan() {
        String pkgName = "com.tencent.mm";

        wechatScan = new QREntity(() -> {
            try {
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
                if (intent != null) {
                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        wechatScan.setPkgName(pkgName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(wechatScanId);
        ae.setAppName("微信扫码");
        ae.setParam1(pkgName);
        ae.setDescription(mContext.getString(R.string.desc_work_at_any_mode));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("0");
        return ae;
    }

    /**
     * Wechat pay page
     */
    public static final String wechatPayId = "wechatPay";
    public QREntity wechatPay;

    private AnywhereEntity genWechatPay() {
        String pkgName = "com.tencent.mm";
        String clsName = ".plugin.offline.ui.WalletOfflineCoinPurseUI";
        String cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, pkgName + clsName);

        wechatPay = new QREntity(() -> CommandUtils.execCmd(cmd));

        wechatPay.setPkgName(pkgName);
        wechatPay.setClsName(clsName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(wechatPayId);
        ae.setAppName("微信支付");
        ae.setParam1(pkgName);
        ae.setParam2(clsName);
        ae.setDescription(mContext.getString(R.string.desc_need_root));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("1");
        return ae;
    }

    /**
     * Wechat collect page
     */
    public static final String wechatCollectId = "wechatCollect";
    public QREntity wechatCollect;

    private AnywhereEntity genWechatCollect() {
        String pkgName = "com.tencent.mm";
        String clsName = ".plugin.collect.ui.CollectMainUI";
        String cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, pkgName + clsName);

        wechatCollect = new QREntity(() -> CommandUtils.execCmd(cmd));

        wechatCollect.setPkgName(pkgName);
        wechatCollect.setClsName(clsName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(wechatCollectId);
        ae.setAppName("微信收款");
        ae.setParam1(pkgName);
        ae.setParam2(clsName);
        ae.setDescription(mContext.getString(R.string.desc_need_root));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("2");
        return ae;
    }

    /**
     * Alipay scan page
     */
    public static final String alipayScanId = "alipayScan";
    public QREntity alipayScan;

    private AnywhereEntity genAlipayScan() {
        String urlScheme = "alipayqr://platformapi/startapp?saId=10000007";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayScan = new QREntity(() -> {
            try {
                URLSchemeHandler.parse(urlScheme, mContext);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });

        alipayScan.setUrlScheme(urlScheme);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(alipayScanId);
        ae.setAppName("支付宝扫码");
        ae.setParam1(pkgName);
        ae.setParam3(urlScheme);
        ae.setDescription(mContext.getString(R.string.desc_work_at_any_mode));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("3");
        return ae;
    }

    /**
     * Alipay pay page
     */
    public static final String alipayPayId = "alipayPay";
    public QREntity alipayPay;

    private AnywhereEntity genAlipayPay() {
        String urlScheme = "alipays://platformapi/startapp?appId=20000056";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayPay = new QREntity(() -> {
            try {
                URLSchemeHandler.parse(urlScheme, mContext);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });

        alipayPay.setUrlScheme(urlScheme);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(alipayPayId);
        ae.setAppName("支付宝付款");
        ae.setParam1(pkgName);
        ae.setParam3(urlScheme);
        ae.setDescription(mContext.getString(R.string.desc_work_at_any_mode));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("4");
        return ae;
    }

    /**
     * Alipay bus page
     */
    public static final String alipayBusId = "alipayBus";
    public QREntity alipayBus;

    private AnywhereEntity genAlipayBus() {
        String urlScheme = "alipayqr://platformapi/startapp?saId=200011235";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayBus = new QREntity(() -> {
            try {
                URLSchemeHandler.parse(urlScheme, mContext);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });

        alipayBus.setUrlScheme(urlScheme);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(alipayBusId);
        ae.setAppName("支付宝公交码");
        ae.setParam1(pkgName);
        ae.setParam3(urlScheme);
        ae.setDescription(mContext.getString(R.string.desc_work_at_any_mode));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("5");
        return ae;
    }

    /**
     * Alipay collect page
     */
    public static final String alipayCollectId = "alipayCollect";
    public QREntity alipayCollect;

    private AnywhereEntity genAlipayCollect() {
        String urlScheme = "alipays://platformapi/startapp?appId=20000123";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayCollect = new QREntity(() -> {
            try {
                URLSchemeHandler.parse(urlScheme, mContext);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });

        alipayCollect.setUrlScheme(urlScheme);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(alipayCollectId);
        ae.setAppName("支付宝收款");
        ae.setParam1(pkgName);
        ae.setParam3(urlScheme);
        ae.setDescription(mContext.getString(R.string.desc_work_at_any_mode));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("6");
        return ae;
    }

    /**
     * QQ scan page
     */
    public static final String qqScanId = "qqScan";
    public QREntity qqScan;

    private AnywhereEntity genQqScan() {
        String pkgName = "com.tencent.mobileqq";
        String clsName = ".olympic.activity.ScanTorchActivity";
        String cmd = String.format(Const.CMD_OPEN_ACTIVITY_FORMAT, pkgName, pkgName + clsName);

        qqScan = new QREntity(() -> CommandUtils.execCmd(cmd));

        qqScan.setPkgName(pkgName);
        qqScan.setClsName(clsName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(qqScanId);
        ae.setAppName("QQ 扫码");
        ae.setParam1(pkgName);
        ae.setParam2(clsName);
        ae.setDescription(mContext.getString(R.string.desc_need_root));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp("7");
        return ae;
    }

    private AnywhereEntity genUnionPay(String id, String text, String priority) {
        String pkgName = "com.unionpay";

        QREntity qrEntity = new QREntity(() -> {
            if (!checkAccessibilityEnabled()) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ToastUtil.makeText(R.string.toast_grant_accessibility);
            } else {
                IzukoService.isClicked(false);
                IzukoService.setPackageName("com.unionpay");
                IzukoService.setClassName("com.unionpay.activity.UPActivityMain");
                IzukoService.setClickText(text);

                try {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    Logger.d("WORKING_MODE_URL_SCHEME:Exception:", e.getMessage());
                }
            }
        });

        qrEntity.setPkgName(pkgName);

        switch (text) {
            case "付款码":
                unionpayPay = qrEntity;
                break;
            case "收款码":
                unionpayCollect = qrEntity;
                break;
            case "扫一扫":
                unionpayScan = qrEntity;
                break;
            default:
        }

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(id);
        ae.setAppName("云闪付" + text);
        ae.setParam1(pkgName);
        ae.setDescription(mContext.getString(R.string.desc_need_accessibility));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp(priority);
        return ae;
    }

    /**
     * UnionPay pay page
     */
    public static final String unionpayPayId = "unionpayPay";
    public QREntity unionpayPay;

    private AnywhereEntity genUnionpayPay() {
        return genUnionPay(unionpayPayId, "付款码", "8");
    }

    /**
     * UnionPay collect page
     */
    public static final String unionpayCollectId = "unionpayCollect";
    public QREntity unionpayCollect;

    private AnywhereEntity genUnionpayCollect() {
        return genUnionPay(unionpayCollectId, "收款码", "9");
    }

    /**
     * UnionPay scan page
     */
    public static final String unionpayScanId = "unionpayScan";
    public QREntity unionpayScan;

    private AnywhereEntity genUnionpayScan() {
        return genUnionPay(unionpayScanId, "扫一扫", "10");
    }

    /**
     * UnionPay signIn page
     */
    public static final String unionpaySignInId = "unionpaySignIn";
    public QREntity unionpaySignIn;

    private AnywhereEntity genUnionpaySignIn() {
        return genUnionPay(unionpaySignInId, "签到", "11");
    }
}
