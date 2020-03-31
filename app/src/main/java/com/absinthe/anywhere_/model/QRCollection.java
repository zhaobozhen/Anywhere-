package com.absinthe.anywhere_.model;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.IzukoService;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.workflow.FlowNode;
import com.absinthe.anywhere_.workflow.WorkFlow;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import timber.log.Timber;

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
    private HashMap<String, QREntity> mMap;
    private int mPriority = 0;

    private QRCollection() {
        mContext = Utils.getApp();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);

        mList = new ArrayList<>();
        mList.add(genWechatScan());
        mList.add(genWechatPay());
        mList.add(genWechatPayAcs());
        mList.add(genWechatCollect());
        mList.add(genWechatCollectAcs());
        mList.add(genAlipayScan());
        mList.add(genAlipayPay());
        mList.add(genAlipayBus());
        mList.add(genAlipayCollect());
        mList.add(genQqScan());
        mList.add(genUnionpayPay());
        mList.add(genUnionpayCollect());
        mList.add(genUnionpayScan());
        mList.add(genUnionpayBus());

        mMap = new HashMap<>();
        mMap.put(wechatScanId, wechatScan);
        mMap.put(wechatPayId, wechatPay);
        mMap.put(wechatPayAcsId, wechatPayAcs);
        mMap.put(wechatCollectId, wechatCollect);
        mMap.put(wechatCollectAcsId, wechatCollectAcs);
        mMap.put(alipayScanId, alipayScan);
        mMap.put(alipayPayId, alipayPay);
        mMap.put(alipayBusId, alipayBus);
        mMap.put(alipayCollectId, alipayCollect);
        mMap.put(qqScanId, qqScan);
        mMap.put(unionpayPayId, unionpayPay);
        mMap.put(unionpayCollectId, unionpayCollect);
        mMap.put(unionpayScanId, unionpayScan);
        mMap.put(unionpayBusId, unionpayBus);
    }

    public ArrayList<AnywhereEntity> getList() {
        return mList;
    }

    public QREntity getQREntity(String id) {
        return mMap.get(id);
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
            Timber.d(info.getId());
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wechat scan page
     */
    private static final String wechatScanId = "wechatScan";
    private QREntity wechatScan;

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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Wechat pay page
     */
    private static final String wechatPayId = "wechatPay";
    private QREntity wechatPay;

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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Wechat pay page Accessibility
     */
    private static final String wechatPayAcsId = "wechatPayAcs";
    private QREntity wechatPayAcs;

    private AnywhereEntity genWechatPayAcs() {
        String pkgName = "com.tencent.mm";
        String clsName = "com.tencent.mm.ui.LauncherUI";

        wechatPayAcs = new QREntity(() -> {
            if (!checkAccessibilityEnabled()) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ToastUtil.makeText(R.string.toast_grant_accessibility);
            } else {
                IzukoService.setPackageName(pkgName);
                IzukoService.setClassName(clsName);
                IzukoService.isClicked(false);
                Observable<FlowNode> source = Observable.create(emitter -> {
                    emitter.onNext(new FlowNode("com.tencent.mm:id/c7", FlowNode.TYPE_ACCESSIBILITY_VIEW_ID));
                    Thread.sleep(200);
                    emitter.onNext(new FlowNode("收付款", FlowNode.TYPE_ACCESSIBILITY_TEXT));

                    emitter.onComplete();
                });
                IzukoService.setWorkFlow(new WorkFlow().observe(source));

                try {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    Timber.d("WORKING_MODE_URL_SCHEME:Exception: %s", e.getMessage());
                }
            }
        });

        wechatPay.setPkgName(pkgName);
        wechatPay.setClsName(clsName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(wechatPayAcsId);
        ae.setAppName("微信支付");
        ae.setParam1(pkgName);
        ae.setParam2(clsName);
        ae.setDescription(mContext.getString(R.string.desc_need_accessibility));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Wechat collect page
     */
    private static final String wechatCollectId = "wechatCollect";
    private QREntity wechatCollect;

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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Wechat collect page Accessibility
     */
    private static final String wechatCollectAcsId = "wechatCollectAcs";
    private QREntity wechatCollectAcs;

    private AnywhereEntity genWechatCollectAcs() {
        String pkgName = "com.tencent.mm";
        String clsName = "com.tencent.mm.ui.LauncherUI";

        wechatCollectAcs = new QREntity(() -> {
            if (!checkAccessibilityEnabled()) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ToastUtil.makeText(R.string.toast_grant_accessibility);
            } else {
                IzukoService.setPackageName(pkgName);
                IzukoService.setClassName(clsName);
                IzukoService.isClicked(false);
                Observable<FlowNode> source = Observable.create(emitter -> {
                    emitter.onNext(new FlowNode("com.tencent.mm:id/c7", FlowNode.TYPE_ACCESSIBILITY_VIEW_ID));
                    Thread.sleep(200);
                    emitter.onNext(new FlowNode("收付款", FlowNode.TYPE_ACCESSIBILITY_TEXT));
                    Thread.sleep(800);
                    IzukoService.setClassName("com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI");
                    emitter.onNext(new FlowNode("二维码收款", FlowNode.TYPE_ACCESSIBILITY_TEXT));

                    emitter.onComplete();
                });
                IzukoService.setWorkFlow(new WorkFlow().observe(source));

                try {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    Timber.d("WORKING_MODE_URL_SCHEME:Exception: %s", e.getMessage());
                }
            }
        });

        wechatPay.setPkgName(pkgName);
        wechatPay.setClsName(clsName);

        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setId(wechatCollectAcsId);
        ae.setAppName("微信收款");
        ae.setParam1(pkgName);
        ae.setParam2(clsName);
        ae.setDescription(mContext.getString(R.string.desc_need_accessibility));
        ae.setType(AnywhereType.QR_CODE);
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Alipay scan page
     */
    private static final String alipayScanId = "alipayScan";
    private QREntity alipayScan;

    private AnywhereEntity genAlipayScan() {
        String urlScheme = "alipayqr://platformapi/startapp?saId=10000007";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayScan = new QREntity(() -> {
            try {
                URLSchemeHandler.INSTANCE.parse(urlScheme, mContext);
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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Alipay pay page
     */
    private static final String alipayPayId = "alipayPay";
    private QREntity alipayPay;

    private AnywhereEntity genAlipayPay() {
        String urlScheme = "alipays://platformapi/startapp?appId=20000056";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayPay = new QREntity(() -> {
            try {
                URLSchemeHandler.INSTANCE.parse(urlScheme, mContext);
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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Alipay bus page
     */
    private static final String alipayBusId = "alipayBus";
    private QREntity alipayBus;

    private AnywhereEntity genAlipayBus() {
        String urlScheme = "alipayqr://platformapi/startapp?saId=200011235";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayBus = new QREntity(() -> {
            try {
                URLSchemeHandler.INSTANCE.parse(urlScheme, mContext);
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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * Alipay collect page
     */
    private static final String alipayCollectId = "alipayCollect";
    private QREntity alipayCollect;

    private AnywhereEntity genAlipayCollect() {
        String urlScheme = "alipays://platformapi/startapp?appId=20000123";
        String pkgName = "com.eg.android.AlipayGphone";

        alipayCollect = new QREntity(() -> {
            try {
                URLSchemeHandler.INSTANCE.parse(urlScheme, mContext);
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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    /**
     * QQ scan page
     */
    private static final String qqScanId = "qqScan";
    private QREntity qqScan;

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
        ae.setTimeStamp(String.valueOf(mPriority++));
        return ae;
    }

    private AnywhereEntity genUnionPay(String id, String text, String priority) {
        String pkgName = "com.unionpay";
        String clsName = "com.unionpay.activity.UPActivityMain";

        QREntity qrEntity = new QREntity(() -> {
            if (!checkAccessibilityEnabled()) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ToastUtil.makeText(R.string.toast_grant_accessibility);
            } else {
                IzukoService.isClicked(false);
                IzukoService.setPackageName(pkgName);
                IzukoService.setClassName(clsName);
                Observable<FlowNode> source = Observable.create(emitter -> {
                    emitter.onNext(new FlowNode("知道了", FlowNode.TYPE_ACCESSIBILITY_TEXT));
                    Thread.sleep(200);
                    emitter.onNext(new FlowNode("跳过", FlowNode.TYPE_ACCESSIBILITY_TEXT));
                    Thread.sleep(200);
                    emitter.onNext(new FlowNode(text, FlowNode.TYPE_ACCESSIBILITY_TEXT));

                    emitter.onComplete();
                });
                IzukoService.setWorkFlow(new WorkFlow().observe(source));

                try {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    Timber.d("WORKING_MODE_URL_SCHEME:Exception: %s", e.getMessage());
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
            case "乘车码":
                unionpayBus = qrEntity;
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
    private static final String unionpayPayId = "unionpayPay";
    private QREntity unionpayPay;

    private AnywhereEntity genUnionpayPay() {
        return genUnionPay(unionpayPayId, "付款码", String.valueOf(mPriority++));
    }

    /**
     * UnionPay collect page
     */
    private static final String unionpayCollectId = "unionpayCollect";
    private QREntity unionpayCollect;

    private AnywhereEntity genUnionpayCollect() {
        return genUnionPay(unionpayCollectId, "收款码", String.valueOf(mPriority++));
    }

    /**
     * UnionPay scan page
     */
    private static final String unionpayScanId = "unionpayScan";
    private QREntity unionpayScan;

    private AnywhereEntity genUnionpayScan() {
        return genUnionPay(unionpayScanId, "扫一扫", String.valueOf(mPriority++));
    }

    /**
     * UnionPay bus page
     */
    private static final String unionpayBusId = "unionpayBus";
    private QREntity unionpayBus;

    private AnywhereEntity genUnionpayBus() {
        return genUnionPay(unionpayBusId, "乘车码", String.valueOf(mPriority++));
    }
}
