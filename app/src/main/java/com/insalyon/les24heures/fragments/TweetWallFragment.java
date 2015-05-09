package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.insalyon.les24heures.R;

/**
 * Created by lucas on 20/04/15.
 */
public class TweetWallFragment extends Fragment {
    private View view;
    private android.webkit.WebView webview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.tweetwall_fragment, container, false);

        webview = (WebView) view.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl(getResources().getString(R.string.tweetWallUrl));


        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                view.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
            }
        });


        return view;
    }
}