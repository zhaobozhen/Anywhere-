package com.absinthe.anywhere_.ui.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.viewmodel.AnywhereObserver;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuService;

public class MainFragment extends Fragment implements LifecycleOwner {

    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISSION_V3 = 2;
    private static final int REQUEST_CODE_AUTHORIZATION_V3 = 3;
    private AnywhereObserver observer;
    private AnywhereViewModel mViewModel;
    private FloatingActionButton fab;
    private TextView tvPackage, tvClass;
    Button button;
    private Context mContext;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = getView().findViewById(R.id.fab);
        tvPackage = getView().findViewById(R.id.tv_package);
        tvClass = getView().findViewById(R.id.tv_class);
        button = getView().findViewById(R.id.button);
        mContext = getContext();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);

        fab.setOnClickListener(view -> checkOverlayPermission());

        button.setOnClickListener(view -> openAnywhereActivity());

        final Observer<String> packageNameObserver = s -> tvPackage.setText(s);

        final Observer<String> classNameObserver = s -> tvClass.setText(s);

        mViewModel.getPackageName().observe(this, packageNameObserver);
        mViewModel.getClassName().observe(this, classNameObserver);

        checkShizukuOnWorking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mViewModel.getPackageName().setValue(bundle.getString("packageName"));
            mViewModel.getClassName().setValue(bundle.getString("className"));
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                startActivityForResult(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName())),
                        REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION
                );
                Toast.makeText(mContext, "请先授予 \"Anywhere-\" 悬浮窗权限", Toast.LENGTH_LONG).show();
            } else {
                startCollector();
            }
        }
    }

    private void startCollector() {
        if (checkShizukuOnWorking()) {
            Intent intent = new Intent(mContext, CollectorService.class);
            intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
            Toast.makeText(getContext(), "已开启Collector", Toast.LENGTH_SHORT).show();
            mContext.startService(intent);
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION && Settings.canDrawOverlays(mContext)) {
            startCollector();
        }
    }

    private void openAnywhereActivity() {
        String packageName = mViewModel.getPackageName().getValue();
        String className = mViewModel.getClassName().getValue();
        String cmd = "am start -n " + packageName + "/" + packageName + className;
        execShizukuWithPermissionCheck(cmd);
    }

    private boolean checkShizukuOnWorking() {
        // Shizuku v3 service will send binder via Content Provider to this process when this activity comes foreground.

        // Wait a few seconds here for binder

        if (!ShizukuService.pingBinder()) {
            if (AnywhereApplication.isShizukuV3Failed()) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                Toast.makeText(mContext, "provider started with no binder included.", Toast.LENGTH_SHORT).show();
            }

            // Shizuku v3 may not running, notify user
            Toast.makeText(mContext, "Shizuku v3 may not running.", Toast.LENGTH_SHORT).show();
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true;
        }
        return false;
    }

    private void execShizukuWithPermissionCheck(String cmd) {
        if (!ShizukuClientHelper.isPreM()) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (ActivityCompat.checkSelfPermission(mContext, ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{ShizukuApiConstants.PERMISSION}, REQUEST_CODE_PERMISSION_V3);
                return;
            }
        } else if (!AnywhereApplication.isShizukuV3TokenValid()){
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelper.createPre23AuthorizationIntent(mContext);
            if (intent != null) {
                try {
                    startActivityForResult(intent, REQUEST_CODE_AUTHORIZATION_V3);
                } catch (Throwable tr) {
                    // should never happened
                }
            } else {
                // activity not found
                Toast.makeText(mContext, "activity not found.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        PermissionUtil.execShizukuCmd(cmd);
    }

}
