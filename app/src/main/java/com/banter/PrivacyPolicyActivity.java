package com.banter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.banter.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.privacyView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.loadingPB.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.loadingPB.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        WebSettings webSettings = binding.privacyView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        binding.privacyView.loadUrl("https://banters.netlify.app/privacy_policy");


    }


    @Override
    public void onBackPressed() {
        if (binding.privacyView.canGoBack()) {
            binding.privacyView.goBack();
//            Intent intent = new Intent(PrivacyPolicyActivity.this, SettingActivity.class);
//            startActivity(intent);
//            finish();
        } else {
            super.onBackPressed();
        }
    }
}