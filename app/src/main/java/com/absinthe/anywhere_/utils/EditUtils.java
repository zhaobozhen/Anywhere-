package com.absinthe.anywhere_.utils;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;

import java.util.List;

public class EditUtils {
    private static final String TAG = "EditUtils";

    public static boolean hasSameAppName(String param1, String param2) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1()) && param2.equals(ae.getParam2())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static AnywhereEntity hasSameAppNameEntity(String param1, String param2) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1()) && param2.equals(ae.getParam2())) {
                    return ae;
                }
            }
        }
        return null;
    }

    public static boolean hasSameAppName(String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static AnywhereEntity hasSameAppNameEntity(String param1) {
        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1())) {
                    return ae;
                }
            }
        }
        return null;
    }
}
