package com.absinthe.anywhere_.utils.handler;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.fragment.DynamicParamsDialogFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.ActivityStackManager;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class Opener {

    private static final int TYPE_ENTITY = 0;
    private static final int TYPE_CMD = 1;

    private static Opener sInstance;
    private static WeakReference<Context> sContext;

    private OnOpenListener mListener;
    private AnywhereEntity mItem;
    private String mCmd;
    private int type;

    private static Opener getInstance() {
        if (sInstance == null) {
            sInstance = new Opener();
        }
        return sInstance;
    }

    public static Opener with(Context context) {
        sContext = new WeakReference<>(context);
        return getInstance();
    }

    public Opener load(AnywhereEntity item) {
        type = TYPE_ENTITY;
        mItem = item;

        return getInstance();
    }

    public Opener load(String cmd) {
        type = TYPE_CMD;
        mCmd = cmd;

        return getInstance();
    }

    public Opener setOpenedListener(OnOpenListener listener) {
        mListener = listener;
        return getInstance();
    }

    public void open() {
        if (type == TYPE_ENTITY) {
            String cmd = TextUtils.getItemCommand(mItem);
            if (!cmd.isEmpty()) {
                if (AppUtils.isAppFrozen(sContext.get(), mItem)) {
                    if (ContextCompat.checkSelfPermission(sContext.get(), IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                        if (PermissionUtils.INSTANCE.isMIUI()) {
                            DialogManager.showGrantPrivilegedPermDialog((AppCompatActivity) sContext.get());
                        } else {
                            ActivityCompat.requestPermissions(ActivityStackManager.INSTANCE.getTopActivity(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
                        }
                    } else {
                        final OnAppUnfreezeListener onAppUnfreezeListener = () ->
                                CommandUtils.execCmd(cmd);
                        if (mItem.getAnywhereType() == AnywhereType.URL_SCHEME) {
                            PermissionUtils.unfreezeApp(sContext.get(), mItem.getParam2(), onAppUnfreezeListener);
                        } else {
                            PermissionUtils.unfreezeApp(sContext.get(), mItem.getParam1(), onAppUnfreezeListener);
                        }
                    }
                } else {
                    CommandUtils.execCmd(cmd);
                }
            }
        } else if (type == TYPE_CMD) {
            Timber.d(mCmd);

            if (mCmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX)) {
                mCmd = mCmd.replace(AnywhereType.DYNAMIC_PARAMS_PREFIX, "");

                int splitIndex = mCmd.indexOf(']');
                String param = mCmd.substring(0, splitIndex);
                mCmd = mCmd.substring(splitIndex + 1);
                DialogManager.showDynamicParamsDialog(ActivityStackManager.INSTANCE.getTopActivity(), param, new DynamicParamsDialogFragment.OnParamsInputListener() {
                    @Override
                    public void onFinish(String text) {
                        openCmd(mCmd + text);
                        if (mListener != null) {
                            mListener.onOpened();
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (mListener != null) {
                            mListener.onOpened();
                        }
                    }
                });
            } else if (mCmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                mCmd = mCmd.replace(AnywhereType.SHELL_PREFIX, "");
                String result = CommandUtils.execAdbCmd(mCmd);
                if (GlobalValues.sIsShowShellResult) {
                    DialogManager.showShellResultDialog(sContext.get(), result, (dialog, which) -> {
                        if (mListener != null) {
                            mListener.onOpened();
                        }
                    }, dialog -> {
                        if (mListener != null) {
                            mListener.onOpened();
                        }
                    });
                } else {
                    mListener.onOpened();
                }
            } else {
                openCmd(mCmd);
            }
        }
    }

    private void openCmd(String cmd) {
        String packageName = TextUtils.getPkgNameByCommand(cmd);
        if (packageName.isEmpty()) {
            CommandUtils.execCmd(cmd);
        } else {
            try {
                if (IceBox.getAppEnabledSetting(sContext.get(), packageName) != 0) {
                    PermissionUtils.unfreezeApp(sContext.get(), packageName, () ->
                            CommandUtils.execCmd(cmd));
                } else {
                    CommandUtils.execCmd(cmd);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                CommandUtils.execCmd(cmd);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                ToastUtil.makeText(R.string.toast_wrong_cmd);
            }
        }
    }

    public interface OnOpenListener {
        void onOpened();
    }
}
