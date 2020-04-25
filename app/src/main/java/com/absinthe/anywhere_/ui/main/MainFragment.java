package com.absinthe.anywhere_.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.BaseAdapter;
import com.absinthe.anywhere_.adapter.ItemTouchCallBack;
import com.absinthe.anywhere_.adapter.card.SelectableCardsAdapter;
import com.absinthe.anywhere_.adapter.card.SingleLineStreamCardsAdapter;
import com.absinthe.anywhere_.adapter.card.StreamCardsAdapter;
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.databinding.FragmentMainBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.manager.ActivityStackManager;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainFragment extends Fragment {
    private static final String BUNDLE_CATEGORY = "CATEGORY";
    private static MutableLiveData<String> sCardMode = new MutableLiveData<>();
    private static boolean sRefreshLock = false;

    private Context mContext;
    private FragmentMainBinding mBinding;
    private String mCategory;

    private RecyclerView mRecyclerView;
    private BaseAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView.LayoutManager mLayoutManager;

    static MainFragment newInstance(String category) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MutableLiveData<String> getCardMode() {
        if (sCardMode == null) {
            sCardMode = new MutableLiveData<>();
        }
        return sCardMode;
    }

    public static void setRefreshLock(boolean lock) {
        sRefreshLock = lock;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(BUNDLE_CATEGORY);
        } else {
            mCategory = GlobalValues.INSTANCE.getCategory();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initObserver();
    }

    private Observer<List<AnywhereEntity>> listObserver = new Observer<List<AnywhereEntity>>() {
        @Override
        public void onChanged(List<AnywhereEntity> anywhereEntities) {
            if (!sRefreshLock) {
                List<AnywhereEntity> filtered = new ArrayList<>();
                for (AnywhereEntity ae : anywhereEntities) {
                    if (ae.getCategory().equals(mCategory)) {
                        filtered.add(ae);
                    }
                }

                if (adapter.getItemCount() == 0) {
                    adapter.setItems(filtered);
                } else {
                    adapter.updateItems(filtered);
                }

                if (!mRecyclerView.canScrollVertically(-1)) {   //Fix Fab cannot be shown after deleting an Anywhere-
                    ((MainActivity) requireActivity()).mBinding.fab.show();
                }
            }
            AppUtils.updateWidget(requireContext());
        }
    };

    private void setUpRecyclerView(RecyclerView recyclerView) {
        ArrayList<AnywhereEntity> anywhereEntityList = new ArrayList<>();

        if (GlobalValues.INSTANCE.isStreamCardMode()) {
            if (GlobalValues.INSTANCE.isStreamCardModeSingleLine()) {
                adapter = new SingleLineStreamCardsAdapter(mContext);
            } else {
                adapter = new StreamCardsAdapter(mContext);
            }
        } else {
            adapter = new SelectableCardsAdapter(mContext);
        }
        adapter.setItems(anywhereEntityList);
        recyclerView.setAdapter(adapter);

        setRecyclerViewLayoutManager(mContext.getResources().getConfiguration());

        ItemTouchCallBack touchCallBack = new ItemTouchCallBack();
        touchCallBack.setOnItemTouchListener(adapter);
        mItemTouchHelper = new ItemTouchHelper(touchCallBack);
        mItemTouchHelper.attachToRecyclerView(null);
    }

    private void refreshRecyclerView() {
        setUpRecyclerView(mRecyclerView);
        List<AnywhereEntity> filtered = new ArrayList<>();
        List<AnywhereEntity> anywhereEntities = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (anywhereEntities != null) {
            for (AnywhereEntity ae : anywhereEntities) {
                if (ae.getCategory().equals(mCategory)) {
                    filtered.add(ae);
                }
            }
            adapter.updateItems(filtered);
        }
    }

    private void resetSelectState() {
        Timber.d("getSelectedIndex() = %s", adapter.getSelectedIndex());
        for (int iter = 0, len = adapter.getItemCount(); iter < len; iter++) {
            View view = mLayoutManager.findViewByPosition(iter);
            if (view != null) {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                ((MaterialCardView) view).setChecked(false);
            }
        }
    }

    private void setRecyclerViewLayoutManager(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (GlobalValues.INSTANCE.isStreamCardMode()) {
                mLayoutManager = new WrapContentStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager = new WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            }
        } else {
            if (GlobalValues.INSTANCE.isStreamCardMode()) {
                mLayoutManager = new WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager = new WrapContentLinearLayoutManager(ActivityStackManager.INSTANCE.getTopActivity());
            }
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRecyclerViewLayoutManager(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_settings).setVisible(adapter.getMode() == SelectableCardsAdapter.ADAPTER_MODE_NORMAL);
        menu.findItem(R.id.toolbar_sort).setVisible(adapter.getMode() == SelectableCardsAdapter.ADAPTER_MODE_NORMAL);
        menu.findItem(R.id.toolbar_done).setVisible(adapter.getMode() != SelectableCardsAdapter.ADAPTER_MODE_NORMAL);
        menu.findItem(R.id.toolbar_delete).setVisible(adapter.getMode() == SelectableCardsAdapter.ADAPTER_MODE_SELECT);

        super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings) {
            startActivity(new Intent(mContext, SettingsActivity.class));
        } else if (item.getItemId() == R.id.toolbar_sort) {
            PopupMenu popup = new PopupMenu(mContext, ((Activity) mContext).findViewById(R.id.toolbar_sort));
            popup.getMenuInflater()
                    .inflate(R.menu.sort_menu, popup.getMenu());
            if (popup.getMenu() instanceof MenuBuilder) {
                MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
                menuBuilder.setOptionalIconsVisible(true);
            }

            switch (GlobalValues.INSTANCE.getSortMode()) {
                default:
                case Const.SORT_MODE_TIME_DESC:
                    popup.getMenu().getItem(0).setChecked(true);
                    break;
                case Const.SORT_MODE_TIME_ASC:
                    popup.getMenu().getItem(1).setChecked(true);
                    break;
                case Const.SORT_MODE_NAME_DESC:
                    popup.getMenu().getItem(2).setChecked(true);
                    break;
                case Const.SORT_MODE_NAME_ASC:
                    popup.getMenu().getItem(3).setChecked(true);
                    break;
            }

            popup.setOnMenuItemClickListener(popupItem -> {
                switch (popupItem.getItemId()) {
                    case R.id.sort_by_time_desc:
                        GlobalValues.INSTANCE.setSortMode(Const.SORT_MODE_TIME_DESC);
                        break;
                    case R.id.sort_by_time_asc:
                        GlobalValues.INSTANCE.setSortMode(Const.SORT_MODE_TIME_ASC);
                        break;
                    case R.id.sort_by_name_desc:
                        GlobalValues.INSTANCE.setSortMode(Const.SORT_MODE_NAME_DESC);
                        break;
                    case R.id.sort_by_name_asc:
                        GlobalValues.INSTANCE.setSortMode(Const.SORT_MODE_NAME_ASC);
                        break;
                    case R.id.sort:
                        adapter.setMode(SelectableCardsAdapter.ADAPTER_MODE_SORT);
                        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                        ((Activity) mContext).invalidateOptionsMenu();
                        mRecyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        break;
                    case R.id.multi_select:
                        adapter.setMode(SelectableCardsAdapter.ADAPTER_MODE_SELECT);
                        ((Activity) mContext).invalidateOptionsMenu();
                        mRecyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        break;
                    default:
                }

                if (popupItem.getItemId() == R.id.sort_by_time_desc ||
                        popupItem.getItemId() == R.id.sort_by_time_asc ||
                        popupItem.getItemId() == R.id.sort_by_name_desc ||
                        popupItem.getItemId() == R.id.sort_by_name_asc) {
                    AnywhereApplication.sRepository.refresh();
                    AnywhereApplication.sRepository.getAllAnywhereEntities().observe(this, listObserver);
                }
                return true;
            });

            popup.show();
        } else if (item.getItemId() == R.id.toolbar_done) {
            if (adapter.getMode() == SelectableCardsAdapter.ADAPTER_MODE_SORT) {
                adapter.setMode(SelectableCardsAdapter.ADAPTER_MODE_NORMAL);
                mItemTouchHelper.attachToRecyclerView(null);
                ((Activity) mContext).invalidateOptionsMenu();
                adapter.updateSortedList();
                GlobalValues.INSTANCE.setSortMode(Const.SORT_MODE_TIME_DESC);
            } else if (adapter.getMode() == SelectableCardsAdapter.ADAPTER_MODE_SELECT) {
                resetSelectState();
                adapter.clearSelect();
                adapter.setMode(SelectableCardsAdapter.ADAPTER_MODE_NORMAL);
                ((Activity) mContext).invalidateOptionsMenu();
            }
            mRecyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        } else if (item.getItemId() == R.id.toolbar_delete) {
            DialogManager.showDeleteSelectCardDialog(mContext, (dialog, which) -> {
                adapter.deleteSelect();
                resetSelectState();
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mRecyclerView = mBinding.recyclerView;
        setUpRecyclerView(mRecyclerView);
        setHasOptionsMenu(true);
    }

    private void initObserver() {
        getCardMode().observe(getViewLifecycleOwner(), s -> refreshRecyclerView());
        AnywhereApplication.sRepository.getAllAnywhereEntities().observe(getViewLifecycleOwner(), listObserver);
    }
}
