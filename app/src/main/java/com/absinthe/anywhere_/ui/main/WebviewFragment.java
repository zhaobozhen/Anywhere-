package com.absinthe.anywhere_.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.ToastUtil;

public class WebviewFragment extends Fragment {

    public WebviewFragment() {
        // Required empty public constructor
    }

    public static WebviewFragment newInstance() {
        return new WebviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Button button = view.findViewById(R.id.btn_open);
        WebView webView = view.findViewById(R.id.wv_container);
        webView.getSettings().setJavaScriptEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/html");
                    getActivity().startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_no_document_app);
                }
            }
        });

        MainActivity.getInstance().setDocumentResultListener(new OnDocumentResultListener() {
            @Override
            public void onResult(Uri uri) {
                webView.loadUrl(uri.toString());
            }
        });
    }
}
