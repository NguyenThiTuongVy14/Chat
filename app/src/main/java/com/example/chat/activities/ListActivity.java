package com.example.chat.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chat.Fragment.Friend_Fragment;
import com.example.chat.Fragment.Message_Fragment;
import com.example.chat.Fragment.Profile_Fragment;
import com.example.chat.R;
import com.example.chat.databinding.ActivityListChatBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private ActivityListChatBinding binding;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentMap.put(R.id.friend, new Friend_Fragment());
        fragmentMap.put(R.id.message, new Message_Fragment());
        fragmentMap.put(R.id.profile, new Profile_Fragment());


        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
            }
            return true;
        });

        binding.bottomNav.setSelectedItemId(R.id.message);
    }
}
