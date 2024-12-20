package com.example.chat.activities;

import static com.example.chat.KEYWORD.KeyWord.KEY_COLECTION_USER;
import static com.example.chat.KEYWORD.KeyWord.KEY_PASS;
import static com.example.chat.KEYWORD.KeyWord.KEY_PHONE;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.HassVerifyPass;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.databinding.ActivitySignUpBinding;
import com.example.chat.firebase.AuthService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

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
                number = binding.txtPhoneSU.getText().toString();

                if (!number.isEmpty() && number.length() == 10) {
                    if (number.charAt(0) == '0') {
                        String pass = binding.txtPassSU.getText().toString();
                        pass= HassVerifyPass.hashPassword(pass);
                        Intent i = new Intent(SignUp.this, VerifyCode.class);
                        i.putExtra("phone", number);
                        i.putExtra("password", pass);
                        i.putExtra("isForgotPassword", false);
                        startActivity(i);
                    } else
                        binding.txtPhoneSU.setError("Must be VietNamese Number Phone");

                } else
                    binding.txtPhoneSU.setError("Number Phone is Invalid");
            }
        });

    }



    private boolean isVailed () {
            String txtPhone = binding.txtPhoneSU.getText().toString().trim();
            String txtRePass = binding.txtRePassSU.getText().toString();
            String txtPass = binding.txtPassSU.getText().toString();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{6,}$";
            if (txtPhone.isEmpty()) {
                binding.txtPhoneSU.setError("Enter Number Phone");
                return false;
            } else if (txtPass.isEmpty()) {
                binding.txtPassSU.setError("Enter Password");
                return false;
            } else if (!Pattern.matches(regex, txtPass)) {
                binding.txtPassSU.setError("Password invalid");
                return false;
            } else if (!txtPass.equals(txtRePass)) {
                binding.txtRePassSU.setError("Incorrect");
                return false;
            } else
                return true;
        }


    }

