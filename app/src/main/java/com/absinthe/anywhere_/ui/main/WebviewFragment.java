package com.absinthe.anywhere_.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.manager.URLManager;

public class WebviewFragment extends Fragment {
    private static final String BUNDLE_URI = "BUNDLE_URI";
    private String mUri;

    public static WebviewFragment newInstance(String uri) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_URI, uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUri = getArguments().getString(BUNDLE_URI);
        } else {
            mUri = URLManager.DOCUMENT_PAGE;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(View view) {
        WebView webView = view.findViewById(R.id.wv_container);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(mUri);
    }
}
