package com.aleenterprise.sg.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aleenterprise.sg.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private static final String EXTRA_COLOR = "color";

    //args
    private int color;
    private WebView mWebView;

    public static Home newInstance(int color) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_COLOR, color);

        Home fragment = new Home();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(EXTRA_COLOR))
            throw new IllegalArgumentException("you should run fragment view newInstance");

        color = getArguments().getInt(EXTRA_COLOR, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = (WebView) getView().findViewById(R.id.homeWebView);
        mWebView.loadUrl("https://www.al-enterprise.com");
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
