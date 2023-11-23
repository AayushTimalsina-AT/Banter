package com.banter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.banter.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.aboutView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.loadingAbout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.loadingAbout.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        binding.aboutView.loadUrl("https://banters.netlify.app/about");
        WebSettings webSettings = binding.aboutView.getSettings();
        webSettings.getJavaScriptEnabled();
    }

    @Override
    public void onBackPressed() {
        if (binding.aboutView.canGoBack()) {
            binding.aboutView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
