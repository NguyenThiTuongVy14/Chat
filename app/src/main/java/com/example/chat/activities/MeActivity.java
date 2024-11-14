package com.example.chat.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.R;
import com.example.chat.databinding.ActivityMainBinding;
import com.example.chat.databinding.ActivityMeBinding;

public class MeActivity extends AppCompatActivity {
    ActivityMeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}