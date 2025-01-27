package com.ctf.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.ctf.demo.databinding.MainBinding;
import com.ctf.demo.databinding.WebviewBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {
    private static final int FLAG_LENGTH = 16;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null && Objects.equals(uri.getHost(), "google.com")) {
            WebviewBinding binding = WebviewBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            WebView webView = binding.webview;
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new JSInterface(), "Android");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    writeFlagToFile();
                }
            });
            webView.loadUrl(uri.toString());
        } else {
            throw new SecurityException("Blocked URI!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void writeFlagToFile() {
        File flagFile = new File(getFilesDir(), "flag.txt");
        if (flagFile.exists()) {
            return;
        }
        String flag = generateSecureFlag();
        try {
            FileOutputStream fos = openFileOutput("flag.txt", MODE_PRIVATE);
            fos.write(flag.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateSecureFlag() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[FLAG_LENGTH];
        secureRandom.nextBytes(randomBytes);
        String randomString = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return "CTF{" + randomString + "}";
    }

    public class JSInterface {
        @android.webkit.JavascriptInterface
        public String readFlag(String path) {
            FileInputStream fis = null;
            String flag = "";
            try {
                fis = openFileInput(path);
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                flag = new String(bytes);
            } catch (IOException e) {
                flag = "Error reading file!";
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return flag;
        }
    }
}
