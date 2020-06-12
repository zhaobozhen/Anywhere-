package com.absinthe.anywhere_.utils.handler;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.model.database.AnywhereEntity;
import com.absinthe.anywhere_.ui.dialog.DynamicParamsDialogFragment;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.lang.ref.WeakReference;

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

    public static Opener with(@NonNull Context context) {
        sContext = new WeakReference<>(context);
        return getInstance();
    }

    public Opener load(@NonNull AnywhereEntity item) {
        type = TYPE_ENTITY;
        mItem = item;

        return getInstance();
    }

    public Opener load(@NonNull String cmd) {
        type = TYPE_CMD;
        mCmd = cmd;

        return getInstance();
    }

    public Opener setOpenedListener(OnOpenListener listener) {
        mListener = listener;
        return getInstance();
    }

    public void open() throws NullPointerException {
        if (sContext.get() == null) {
            throw new NullPointerException("got a null context from Opener#with.");
        }

        if (type == TYPE_ENTITY) {
            openFromEntity(mItem);
        } else if (type == TYPE_CMD) {
            openFromCommand();
        }
    }

    private void openFromEntity(AnywhereEntity item) {
        String cmd = TextUtils.getItemCommand(item);
        openCmd(cmd);
    }

    private void openFromCommand() {
        if (mCmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX)) {
            openDynamicParamCommand(mCmd);
        } else if (mCmd.startsWith(AnywhereType.SHELL_PREFIX)) {
            openShellCommand(mCmd);
        } else {
            openCmd(mCmd);
        }
    }

    private void openDynamicParamCommand(@NonNull String command) {
        String newCommand = command.replace(AnywhereType.DYNAMIC_PARAMS_PREFIX, "");

        int splitIndex = newCommand.indexOf(']');
        String param = newCommand.substring(0, splitIndex);
        newCommand = newCommand.substring(splitIndex + 1);

        String finalNewCommand = newCommand;
        DialogManager.showDynamicParamsDialog((AppCompatActivity) sContext.get(), param, new DynamicParamsDialogFragment.OnParamsInputListener() {
            @Override
            public void onFinish(String text) {
                openCmd(finalNewCommand + text);
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
    }

    private void openShellCommand(@NonNull String command) {
        String newCommand = command.replace(AnywhereType.SHELL_PREFIX, "");
        String result = CommandUtils.execAdbCmd(newCommand);

        if (GlobalValues.INSTANCE.isShowShellResult()) {
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
    }

    private void openCmd(String cmd) {
        if (cmd.isEmpty()) {
            return;
        }

        String packageName = TextUtils.getPkgNameByCommand(cmd);

        if (packageName.isEmpty()) {
            CommandUtils.execCmd(cmd);
        } else {
            try {
                if (IceBox.getAppEnabledSetting(sContext.get(), packageName) != 0) {
                    DefrostHandler.defrost(sContext.get(), packageName, () -> CommandUtils.execCmd(cmd));
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
