package com.absinthe.anywhere_.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.CommandUtils;

import java.util.ArrayList;

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
    private ArrayList<AnywhereEntity> mList;

    private QRCollection() {
        mContext = AnywhereApplication.sContext;
        mList = new ArrayList<>();
        mList.add(genWechatScan());
        mList.add(genWechatPay());
        mList.add(genWechatCollect());
        mList.add(genAlipayScan());
        mList.add(genAlipayPay());
        mList.add(genAlipayBus());
        mList.add(genAlipayCollect());
        mList.add(genQqScan());
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
            default:
                return null;
        }
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

        return AnywhereEntity.Builder()
                .setId(wechatScanId)
                .setAppName("微信扫码")
                .setParam1(pkgName)
                .setDescription(mContext.getString(R.string.desc_work_at_any_mode))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("0");
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

        return AnywhereEntity.Builder()
                .setId(wechatPayId)
                .setAppName("微信支付")
                .setParam1(pkgName)
                .setParam2(clsName)
                .setDescription(mContext.getString(R.string.desc_need_root))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("1");
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

        return AnywhereEntity.Builder()
                .setId(wechatCollectId)
                .setAppName("微信收款")
                .setParam1(pkgName)
                .setParam2(clsName)
                .setDescription(mContext.getString(R.string.desc_need_root))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("2");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(urlScheme));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alipayScan.setUrlScheme(urlScheme);

        return AnywhereEntity.Builder()
                .setId(alipayScanId)
                .setAppName("支付宝扫码")
                .setParam1(pkgName)
                .setParam3(urlScheme)
                .setDescription(mContext.getString(R.string.desc_work_at_any_mode))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("3");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(urlScheme));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alipayPay.setUrlScheme(urlScheme);

        return AnywhereEntity.Builder()
                .setId(alipayPayId)
                .setAppName("支付宝付款")
                .setParam1(pkgName)
                .setParam3(urlScheme)
                .setDescription(mContext.getString(R.string.desc_work_at_any_mode))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("4");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(urlScheme));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alipayBus.setUrlScheme(urlScheme);

        return AnywhereEntity.Builder()
                .setId(alipayBusId)
                .setAppName("支付宝公交码")
                .setParam1(pkgName)
                .setParam3(urlScheme)
                .setDescription(mContext.getString(R.string.desc_work_at_any_mode))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("5");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(urlScheme));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alipayCollect.setUrlScheme(urlScheme);

        return AnywhereEntity.Builder()
                .setId(alipayCollectId)
                .setAppName("支付宝收款")
                .setParam1(pkgName)
                .setParam3(urlScheme)
                .setDescription(mContext.getString(R.string.desc_work_at_any_mode))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("6");
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

        return AnywhereEntity.Builder()
                .setId(qqScanId)
                .setAppName("QQ 扫码")
                .setParam1(pkgName)
                .setParam2(clsName)
                .setDescription(mContext.getString(R.string.desc_need_root))
                .setType(AnywhereType.QR_CODE)
                .setTimeStamp("7");
    }
}
