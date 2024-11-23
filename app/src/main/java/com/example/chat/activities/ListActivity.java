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
    Friend_Fragment fragment1;
    Message_Fragment fragment2;
    Profile_Fragment fragment3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        fragment1 = new Friend_Fragment();
        fragment2 = new Message_Fragment();
        fragment3 = new Profile_Fragment();
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(v -> loadActivity1());
        button2.setOnClickListener(v -> loadActivity2());
        button3.setOnClickListener(v -> loadActivity3());
    }


    private void loadActivity1() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment1);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadActivity2() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment2);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadActivity3() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment3);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}



