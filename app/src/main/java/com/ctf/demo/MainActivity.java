package com.ctf.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import com.ctf.demo.databinding.MainBinding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;

public class MainActivity extends AppCompatActivity {
    private static final int FLAG_LENGTH = 16;
    private MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mirror Challenge");
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        writeFlagToFile();
        RelativeLayout hintBubble = binding.bubbleContainer;
        RelativeLayout textboxContainer = binding.textboxContainer;
        Button showHiddenButton = binding.showHintButton;
        Button doneButton = binding.doneButton;
        Button submitButton = binding.submitFlagButton;
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party =
                new PartyFactory(emitterConfig)
                        .angle(270)
                        .spread(90)
                        .setSpeedBetween(1f, 5f)
                        .timeToLive(2000L)
                        .sizes(new Size(12, 5f, 0.2f))
                        .position(0.0, 0.0, 1.0, 0.0)
                        .build();
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintBubble.setVisibility(View.INVISIBLE);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textboxContainer.setVisibility(View.VISIBLE);
            }
        });
        showHiddenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintBubble.setVisibility(View.VISIBLE);
            }
        });
        EditText inputTextBox = findViewById(R.id.inputTextBox);
        inputTextBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String enteredText = inputTextBox.getText().toString().trim();
                if (readFlagFromFile().equalsIgnoreCase(enteredText)) {
                    binding.konfettiView.start(party);
                    Toast.makeText(MainActivity.this, "Correct input: secret!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect secret. Try again!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
        if (Intent.ACTION_VIEW.equals(getIntent().getAction()) && getIntent().getData() != null) {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.setData(intent.getData());
            startActivity(intent);
        }
    }

    private void writeFlagToFile() {
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

    private String readFlagFromFile() {
        FileInputStream fis = null;
        String flag = "";
        try {
            fis = openFileInput("flag.txt");
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            flag = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
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
