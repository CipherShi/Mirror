package com.ctf.demo;

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
import java.io.FileInputStream;
import java.io.IOException;

public class WebViewActivity extends AppCompatActivity {
    private WebviewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WebviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        WebView webView = binding.webview;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith("https://google.com")) {
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(uri.toString());
        } else {
            throw new SecurityException("Blocked URI!");
        }
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
