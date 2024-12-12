package com.example.chat.activities;

import static com.example.chat.KEYWORD.KeyWord.KEY_COLECTION_USER;
import static com.example.chat.KEYWORD.KeyWord.KEY_PASS;
import static com.example.chat.KEYWORD.KeyWord.KEY_PHONE;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.Preference.PreferencManager;
import com.example.chat.databinding.ActivitySignUpBinding;
import com.example.chat.firebase.AuthService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity{

    ActivitySignUpBinding binding;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListens();
    }

    private void setListens() {
        binding.backLogin.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(v -> {
            if (isVailed()) {
                number=binding.txtEmailSU.getText().toString();

                if (!number.isEmpty() && number.length() == 10) {
                    if (number.charAt(0)=='0'){
                        String pass= binding.txtPassSU.getText().toString();
                        Intent i = new Intent(SignUp.this,VerifyCode.class);
                        i.putExtra("phone",number);
                        i.putExtra("password",pass);
                        startActivity(i);
                    }
                    else
                        binding.txtEmailSU.setError("Must be VietNamese Number Phone");

                }
                else
                    binding.txtEmailSU.setError("Number Phone is Invalid");
            }
        });

    }

    private boolean isVailed() {
        if (binding.txtEmailSU.getText().toString().trim().isEmpty())
        {
            binding.txtEmailSU.setError("Enter Number Phone");
            return false;
        }
        else if (binding.txtPassSU.getText().toString().trim().isEmpty())
        {
            binding.txtPassSU.setError("Enter Password");
            return false;
        }
        else
            return true;
    }
    private void loadActivity(Boolean load){
        if(load){
            binding.btnSignUp.setVisibility(View.INVISIBLE);

        }

        else {
            binding.btnSignUp.setVisibility(View.VISIBLE);
        }
    }

}