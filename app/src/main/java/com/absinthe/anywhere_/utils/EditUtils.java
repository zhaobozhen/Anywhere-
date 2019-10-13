package com.absinthe.anywhere_.utils;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;

import java.util.List;

public class EditUtils {
    private static final String TAG = "EditUtils";

    public static boolean hasSameAppName(String name, String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (name.equals(ae.getAppName()) && param1.equals(ae.getParam1())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static AnywhereEntity hasSameAppNameEntity(String name, String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (name.equals(ae.getAppName()) && param1.equals(ae.getParam1())) {
                    return ae;
                }
            }
        }
        return null;
    }

}
