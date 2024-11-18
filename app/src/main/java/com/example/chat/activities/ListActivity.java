package com.example.chat.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chat.Fragment.Friend_Fragment;
import com.example.chat.Fragment.Message_Fragment;
import com.example.chat.Fragment.Profile_Fragment;
import com.example.chat.R;

public class ListActivity extends AppCompatActivity {
    private Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        // Ánh xạ các nút
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        // Set sự kiện cho các nút
        button1.setOnClickListener(v -> loadActivity1());
        button2.setOnClickListener(v -> loadActivity2());
        button3.setOnClickListener(v -> loadActivity3());
    }

    // Hàm thay đổi nội dung bằng Fragment
    private void loadActivity1() {
        Friend_Fragment fragment1 = new Friend_Fragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment1);
        transaction.addToBackStack(null); // Để có thể quay lại Fragment cũ nếu cần
        transaction.commit();
    }

    private void loadActivity2() {
        Message_Fragment fragment2 = new Message_Fragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment2);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadActivity3() {
        Profile_Fragment fragment3 = new Profile_Fragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment3);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}



