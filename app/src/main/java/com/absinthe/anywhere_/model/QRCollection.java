package com.absinthe.anywhere_.model;

import android.content.Context;
import android.content.Intent;

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
        mList = new ArrayList<>();
        mList.add(genWechatScan());
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public ArrayList<AnywhereEntity> getList() {
        return mList;
    }

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

        return new AnywhereEntity("", "Wechat Scan", pkgName,
                "", "", "",
                AnywhereType.QR_CODE, "");
    }
}
