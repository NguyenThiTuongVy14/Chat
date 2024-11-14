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

public class SignUp extends AppCompatActivity implements AuthService.AuthCallback{

    ActivitySignUpBinding binding;
    PreferencManager preferencManager;
    AuthService auth;
    Handler hd;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        hd = new Handler();
        setContentView(binding.getRoot());
        preferencManager = new PreferencManager(getApplicationContext());
        auth=new AuthService(this);
        setListens();
    }


    private void setListens() {
        binding.backLogin.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(v -> {
//            if (isVailed()) {
//                if(binding.vertify.getText().toString().length()==6){
//                    auth.verifyOtp(binding.vertify.getText().toString());
//                }
//
//            }
            startActivity(new Intent(SignUp.this,VerifyCode.class));
        });
//        binding.sendCode.setOnClickListener(v -> {
//            number=binding.txtEmailSU.getText().toString();
//
//            if (!number.isEmpty() && number.length() == 10) {
//                if (number.charAt(0)=='0'){
//                    number=formatNumberPhone(number);
//                    auth.sendOtp(number, this);
//
//                }
//                else
//                    binding.txtEmailSU.setError("Must be VietNamese Number Phone");
//
//            }
//            else
//                binding.txtEmailSU.setError("Number Phone is Invalid");
//
//        });
    }
    private String formatNumberPhone(String number){
        if(number.length()==10 && number.charAt(0)=='0')
            number="+84"+number.substring(1);
        return number;
    }

    private void signUp() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String phone = binding.txtEmailSU.getText().toString();
        String password = binding.txtPassSU.getText().toString();
        db.collection(KEY_COLECTION_USER)
                .whereEqualTo(KEY_PHONE, phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                        HashMap<String, Object> user = new HashMap<>();
                        user.put(KEY_PHONE, phone);
                        user.put(KEY_PASS, password);
                        db.collection(KEY_COLECTION_USER).add(user)
                                .addOnSuccessListener(documentReference -> {
                                    showToast("Sign Up is Successful");
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(exception -> {
                                    showToast("Sign Up Fail");
                                });
                    } else {
                        showToast("Phone number already exists");
                    }
                });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isVailed() {
        if (binding.txtEmailSU.getText().toString().trim().isEmpty())
        {
            binding.txtEmailSU.setError("Enter Number Phone");
            return false;
        }
        else if (binding.txtPassSU.getText().toString().trim().isEmpty())
        {
            binding.txtEmailSU.setError("Enter Password");
            return false;
        }
//        else if (binding.vertify.getText().toString().trim().isEmpty())
//        {
//            binding.txtEmailSU.setError("Enter OTP Code");
//            return false;
//        }
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
    @Override
    public void onCodeSent(String verificationId) {
        showToast("OTP was sent");
    }
    @Override
    public void onVerificationFailed(String errorMessage) {
    }
    @Override
    public void onSignInSuccess(FirebaseUser user) {
        signUp();
    }
    @Override
    public void onSignInFailed(String errorMessage) {
//        binding.vertify.setError("Code is incorrect");
    }
}