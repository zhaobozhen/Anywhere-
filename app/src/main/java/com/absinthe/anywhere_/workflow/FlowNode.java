package com.absinthe.anywhere_.workflow;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.services.IzukoService;
import com.absinthe.anywhere_.utils.manager.Logger;

public class FlowNode {
    public static final int TYPE_ACCESSIBILITY_TEXT = 0;
    public static final int TYPE_ACCESSIBILITY_VIEW_ID = 1;

    private int type;
    private String content;

    public FlowNode(String text, int type) {
        this.content = text;
        this.type = type;
    }

    public void trigger() {
        Logger.d("trigger");
        switch (type) {
            case TYPE_ACCESSIBILITY_TEXT:
                IzukoService.isClicked(false);
                IzukoService.sInstance.clickTextViewByText(content);
                IzukoService.isClicked(true);
                break;
            case TYPE_ACCESSIBILITY_VIEW_ID:
                IzukoService.isClicked(false);
                IzukoService.sInstance.clickTextViewByID(content);
                IzukoService.isClicked(true);
                break;
            default:
        }
    }

    @NonNull
    @Override
    public String toString() {
        return content;
    }
}
