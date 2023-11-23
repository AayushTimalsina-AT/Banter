package com.banter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.banter.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.helpView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.loadingPBH.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.loadingPBH.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        WebSettings webSettings = binding.helpView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        binding.helpView.loadUrl("https://banters.netlify.app/helps");

    }

    @Override
    public void onBackPressed() {
        if (binding.helpView.canGoBack()) {
            binding.helpView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}