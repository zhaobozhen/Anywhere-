package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

import java.util.ArrayList;
import java.util.List;

public class DynamicParamsDialogBuilder extends ViewBuilder {

    private List<EditText> editTextList = new ArrayList<>();
    private String[] params;

    public DynamicParamsDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);
        int padding = (int) mResources.getDimension(R.dimen.bsd_edit_text_margin_horizontal);
        root.setPadding(padding, 0, padding, 0);
        ((LinearLayout) root).setOrientation(LinearLayout.VERTICAL);
    }

    public void setParams(String paramString) {
        Logger.d(paramString);
        params = paramString.split("&");

        for (String para : params) {
            Logger.d(para);
            EditText editText = new EditText(mContext);
            editText.setLayoutParams(Params.LL.MATCH_WRAP);
            editText.setSingleLine(true);
            editText.setHint(para);
            addView(editText);
            editTextList.add(editText);
        }
    }

    public String getInputParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (int iter = 0, len = params.length; iter < len; iter++) {
            sb.append(params[iter])
                    .append("=")
                    .append(editTextList.get(iter).getText() == null
                            ? ""
                            : editTextList.get(iter).getText().toString());
            if (iter != len - 1) {
                sb.append("&");
            }
        }
        Logger.d(sb);
        return sb.toString();
    }
}
