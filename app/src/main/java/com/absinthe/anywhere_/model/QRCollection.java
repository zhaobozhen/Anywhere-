package com.absinthe.anywhere_.model;

import android.content.Context;
import android.content.Intent;

import com.absinthe.anywhere_.AnywhereApplication;
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

        return new AnywhereEntity(wechatScanId, "Wechat Scan", pkgName,
                "", "", "Work at any mode",
                AnywhereType.QR_CODE, "0");
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

        return new AnywhereEntity(wechatPayId, "Wechat Pay", pkgName,
                clsName, "", "Need Root",
                AnywhereType.QR_CODE, "1");
    }
}
