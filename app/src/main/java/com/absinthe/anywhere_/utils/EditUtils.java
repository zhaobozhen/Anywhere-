package com.absinthe.anywhere_.utils;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;

import java.util.List;

public class EditUtils {

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @param param2 param2
     * @return true if has same Anywhere-
     */
    public static boolean hasSameAppName(String param1, String param2) {
        AnywhereViewModel viewModel = MainFragment.getViewModelInstance();
        if (viewModel == null || viewModel.getAllAnywhereEntities() == null) {
            return false;
        }

        List<AnywhereEntity> list = viewModel.getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1()) && param2.equals(ae.getParam2())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @param param2 param2
     * @return true if has same Anywhere-
     */
    public static AnywhereEntity hasSameAppNameEntity(String param1, String param2) {
        AnywhereViewModel viewModel = MainFragment.getViewModelInstance();
        if (viewModel == null || viewModel.getAllAnywhereEntities() == null) {
            return null;
        }

        List<AnywhereEntity> list = viewModel.getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1()) && param2.equals(ae.getParam2())) {
                    return ae;
                }
            }
        }
        return null;
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @return true if has same Anywhere-
     */
    public static boolean hasSameAppName(String param1) {
        AnywhereViewModel viewModel = MainFragment.getViewModelInstance();
        if (viewModel == null || viewModel.getAllAnywhereEntities() == null) {
            return false;
        }

        List<AnywhereEntity> list = viewModel.getAllAnywhereEntities().getValue();

        if (list != null) {
            for (AnywhereEntity ae : list) {
                if (param1.equals(ae.getParam1())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Judge that whether there has the same Anywhere-
     *
     * @param param1 param1
     * @return true if has same Anywhere-
     */
    public static AnywhereEntity hasSameAppNameEntity(String param1) {
        AnywhereViewModel viewModel = MainFragment.getViewModelInstance();
        if (viewModel == null || viewModel.getAllAnywhereEntities() == null) {
            return null;
        }

        List<AnywhereEntity> list = viewModel.getAllAnywhereEntities().getValue();

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
