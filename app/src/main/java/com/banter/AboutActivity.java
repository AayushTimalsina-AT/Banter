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
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up WebView
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

        // Enable JavaScript
        WebSettings webSettings = binding.aboutView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);

        // Load the URL
        binding.aboutView.loadUrl("https://banters.netlify.app/about");
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
