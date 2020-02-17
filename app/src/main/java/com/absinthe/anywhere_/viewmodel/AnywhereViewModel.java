package com.absinthe.anywhere_.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.view.editor.Editor;
import com.absinthe.anywhere_.view.editor.ImageEditor;
import com.absinthe.anywhere_.view.editor.SchemeEditor;
import com.absinthe.anywhere_.view.editor.ShellEditor;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.ArrayList;
import java.util.List;

public class AnywhereViewModel extends AndroidViewModel {

    private AnywhereRepository mRepository;
    private LiveData<List<AnywhereEntity>> mAllAnywhereEntities;

    private MutableLiveData<String> mWorkingMode = null;
    private MutableLiveData<String> mBackground = null;
    private MutableLiveData<String> mCardMode = null;

    public boolean refreshLock = false;

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

    public MutableLiveData<String> getWorkingMode() {
        if (mWorkingMode == null) {
            mWorkingMode = new MutableLiveData<>();
        }
        return mWorkingMode;
    }

    public MutableLiveData<String> getBackground() {
        if (mBackground == null) {
            mBackground = new MutableLiveData<>();
        }
        return mBackground;
    }

    public MutableLiveData<String> getCardMode() {
        if (mCardMode == null) {
            mCardMode = new MutableLiveData<>();
        }
        return mCardMode;
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
        ae.setAppName(MainActivity.getInstance().getString(R.string.bsd_new_url_scheme_name));
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

    public void checkWorkingPermission(Activity activity) {
        if (GlobalValues.sWorkingMode != null) {
            switch (GlobalValues.sWorkingMode) {
                case Const.WORKING_MODE_URL_SCHEME:
                    setUpUrlScheme(activity);
                    break;
                case Const.WORKING_MODE_SHIZUKU:
                    if (!PermissionUtils.checkOverlayPermission(activity, Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtils.checkShizukuOnWorking(activity) && PermissionUtils.shizukuPermissionCheck(activity)) {
                        startCollector(activity);
                    }
                    break;
                case Const.WORKING_MODE_ROOT:
                    if (!PermissionUtils.checkOverlayPermission(activity, Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        return;
                    }
                    if (PermissionUtils.upgradeRootPermission(activity.getPackageCodePath())) {
                        startCollector(activity);
                    } else {
                        Logger.d("ROOT permission denied.");
                        ToastUtil.makeText(R.string.toast_root_permission_denied);
                    }
                    break;
                default:
            }
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

    private void startCollector(Activity activity) {
        CollectorService.startCollector(activity);
    }

}
