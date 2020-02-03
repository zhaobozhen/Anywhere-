package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

        List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (list != null) {
            for (AnywhereEntity ae : list) {
                ae.setType(ae.getAnywhereType() + ae.getExportedType() * 100);
            }
            String s = gson.toJson(list);
            Logger.d(s);
            return s;
        }
        return null;
    }

    public static void storageToken(Context context, String token) throws IOException {
        String fileName = "Token";

        File file = new File(context.getFilesDir(), fileName);
        if(file.exists()) {
            return;
        }

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(token.getBytes());
        fos.close();
    }

    public static String getTokenFromFile(Context context) throws IOException {
        String fileName = "Token";

        File file = new File(context.getFilesDir(), fileName);
        if(!file.exists()) {
            return "";
        }

        FileInputStream fis = context.openFileInput(fileName);

        if (fis.available() == 0) {
            return "";
        }

        byte[] buffer = new byte[fis.available()];
        while (fis.read(buffer) != -1) { }
        fis.close();
        return new String(buffer);
    }
}
