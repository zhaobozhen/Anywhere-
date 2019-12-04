package com.absinthe.anywhere_.utils;

import android.content.Intent;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.SerializableAnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class StorageUtils {
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Create a file
     *
     * @param activity context
     * @param mimeType MIME type of the file
     * @param fileName file name
     */
    public static void createFile(AppCompatActivity activity, String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        activity.startActivityForResult(intent, Const.REQUEST_CODE_WRITE_FILE);
    }

    /**
     * Export Anywhere- entities to json string
     *
     * @return json string
     */
    public static String ExportAnywhereEntityJsonString() {
        Gson gson = new Gson();

        List<AnywhereEntity> list = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();
        List<SerializableAnywhereEntity> expoList = new ArrayList<>();
        if (list != null) {
            for (AnywhereEntity ae : list) {
                SerializableAnywhereEntity sae = new SerializableAnywhereEntity(ae);
                sae.setmType(ae.getAnywhereType() + ae.getExportedType() * 100);
                expoList.add(sae);
            }
            String s = gson.toJson(expoList);
            Logger.d(s);
            return s;
        }
        return null;
    }
}
