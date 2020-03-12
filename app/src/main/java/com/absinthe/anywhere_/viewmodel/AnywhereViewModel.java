package com.absinthe.anywhere_.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.page.PageNode;
import com.absinthe.anywhere_.adapter.page.PageTitleNode;
import com.absinthe.anywhere_.database.AnywhereRepository;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.ShizukuHelper;
import com.absinthe.anywhere_.view.editor.Editor;
import com.absinthe.anywhere_.view.editor.ImageEditor;
import com.absinthe.anywhere_.view.editor.SchemeEditor;
import com.absinthe.anywhere_.view.editor.ShellEditor;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.ArrayList;
import java.util.List;

public class AnywhereViewModel extends AndroidViewModel {

    private AnywhereRepository mRepository;
    private LiveData<List<AnywhereEntity>> mAllAnywhereEntities;

    private MutableLiveData<String> mBackground = new MutableLiveData<>();
    private MutableLiveData<Fragment> mFragment = new MutableLiveData<>();

    public AnywhereViewModel(Application application) {
        super(application);
        mRepository = AnywhereApplication.sRepository;
        mAllAnywhereEntities = mRepository.getAllAnywhereEntities();
    }

    public LiveData<List<AnywhereEntity>> getAllAnywhereEntities() {
        return mAllAnywhereEntities;
    }

    public void insert(AnywhereEntity ae) {
        mRepository.insert(ae);
    }

    public void update(AnywhereEntity ae) {
        mRepository.update(ae);
    }

    public void delete(AnywhereEntity ae) {
        mRepository.delete(ae);
    }

    public MutableLiveData<String> getBackground() {
        if (mBackground == null) {
            mBackground = new MutableLiveData<>();
        }
        return mBackground;
    }

    public MutableLiveData<Fragment> getFragment() {
        if (mFragment == null) {
            mFragment = new MutableLiveData<>();
        }
        return mFragment;
    }

    public PageTitleNode getEntity(String title) {
        List<BaseNode> pageNodeList = new ArrayList<>();
        PageNode pageNode = new PageNode();
        pageNode.setTitle(title);
        pageNodeList.add(pageNode);
        PageTitleNode pageTitle = new PageTitleNode(pageNodeList, title);
        pageTitle.setExpanded(title.equals(GlobalValues.sCategory));
        return pageTitle;
    }

    public void setUpUrlScheme(Context context, String url) {
        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setAppName(getApplication().getString(R.string.bsd_new_url_scheme_name));
        ae.setParam1(url);
        ae.setType(AnywhereType.URL_SCHEME);

        Editor editor = new SchemeEditor(context)
                .item(ae)
                .isEditorMode(false)
                .isShortcut(false)
                .build();
        editor.show();
    }

    public void setUpUrlScheme(Context context) {
        setUpUrlScheme(context, "");
    }

    public void openImageEditor(Context context, boolean isDismissParent) {
        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setAppName("New Image");
        ae.setType(AnywhereType.IMAGE);

        Editor editor = new ImageEditor(context)
                .item(ae)
                .isEditorMode(false)
                .isShortcut(false)
                .setDismissParent(isDismissParent)
                .build();
        editor.show();
    }

    public void openShellEditor(Context context, boolean isDismissParent) {
        AnywhereEntity ae = AnywhereEntity.Builder();
        ae.setAppName("New Shell");
        ae.setType(AnywhereType.SHELL);

        Editor editor = new ShellEditor(context)
                .item(ae)
                .isEditorMode(false)
                .isShortcut(false)
                .setDismissParent(isDismissParent)
                .build();
        editor.show();
    }

    public void startCollector(Activity activity) {
        switch (GlobalValues.getWorkingMode()) {
            case Const.WORKING_MODE_URL_SCHEME:
                setUpUrlScheme(activity);
                break;
            case Const.WORKING_MODE_SHIZUKU:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (ShizukuHelper.checkShizukuOnWorking(activity) && ShizukuHelper.isGrantShizukuPermission()) {
                        CollectorService.startCollector(activity);
                    } else {
                        ShizukuHelper.requestShizukuPermission();
                    }
                } else {
                    PermissionUtils.requestDrawOverlays(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            if (ShizukuHelper.checkShizukuOnWorking(activity) && ShizukuHelper.isGrantShizukuPermission()) {
                                CollectorService.startCollector(activity);
                            } else {
                                ShizukuHelper.requestShizukuPermission();
                            }
                        }

                        @Override
                        public void onDenied() {

                        }
                    });
                }
                break;
            case Const.WORKING_MODE_ROOT:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (DeviceUtils.isDeviceRooted()) {
                        CollectorService.startCollector(activity);
                    } else {
                        Logger.d("ROOT permission denied.");
                        ToastUtil.makeText(R.string.toast_root_permission_denied);
                        com.absinthe.anywhere_.utils.PermissionUtils.upgradeRootPermission(activity.getPackageCodePath());
                    }
                } else {
                    PermissionUtils.requestDrawOverlays(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            if (DeviceUtils.isDeviceRooted()) {
                                CollectorService.startCollector(activity);
                            } else {
                                Logger.d("ROOT permission denied.");
                                ToastUtil.makeText(R.string.toast_root_permission_denied);
                                com.absinthe.anywhere_.utils.PermissionUtils.upgradeRootPermission(activity.getPackageCodePath());
                            }
                        }

                        @Override
                        public void onDenied() {

                        }
                    });
                }
                break;
            default:
        }
    }

    public void addPage() {
        List<PageEntity> list = mRepository.getAllPageEntities().getValue();
        if (list != null) {
            if (list.size() != 0) {
                int size = list.size();
                PageEntity pe = PageEntity.Builder();
                pe.setTitle("Page " + (size + 1));
                pe.setPriority(size + 1);
                pe.setType(AnywhereType.CARD_PAGE);
                mRepository.insertPage(pe);
            } else {
                PageEntity pe = PageEntity.Builder();
                pe.setTitle(AnywhereType.DEFAULT_CATEGORY);
                pe.setPriority(1);
                pe.setType(AnywhereType.CARD_PAGE);
                mRepository.insertPage(pe);
            }
        }
    }

    public void addWebPage(Uri uri, Intent intent) {
        List<PageEntity> list = mRepository.getAllPageEntities().getValue();
        if (list != null) {
            int size = list.size();
            PageEntity pe = PageEntity.Builder();
            pe.setTitle("Web Page " + (size + 1));
            pe.setPriority(size + 1);
            pe.setType(AnywhereType.WEB_PAGE);
            pe.setExtra(uri.toString());
            mRepository.insertPage(pe);
            AppUtils.takePersistableUriPermission(getApplication(), uri, intent);
        }
    }
}
