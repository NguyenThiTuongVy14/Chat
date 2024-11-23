package com.example.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    PreferencManager preferencManager;
    DocumentSnapshot doc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencManager = new PreferencManager(getApplicationContext());
        preferencManager.clear();
        if(preferencManager.getBool(KeyWord.KEY_IS_LOGIN)){
            startMainActivity();

        }
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListens();
        
    }

    private void login(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection(KeyWord.KEY_COLECTION_USER)
                .whereEqualTo(KeyWord.KEY_PHONE, binding.txtEmailSI.getText().toString())
                .whereEqualTo(KeyWord.KEY_PASS, binding.txtPassSI.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null && task.getResult().getDocuments().size()>0){
                        doc =task.getResult().getDocuments().get(0);
                        preferencManager.putBool(KeyWord.KEY_IS_LOGIN,true);
                        preferencManager.putString(KeyWord.KEY_USERID, doc.getId());
                        preferencManager.putString(KeyWord.KEY_PHONE,doc.getString(KeyWord.KEY_PHONE));
                        preferencManager.putString(KeyWord.KEY_FULL_NAME, doc.getString(KeyWord.KEY_FULL_NAME));
                        preferencManager.putString("image", doc.getString("image"));
                        preferencManager.putBool(KeyWord.KEY_IS_SET_PROFILE,doc.getBoolean(KeyWord.KEY_IS_SET_PROFILE));
                        startMainActivity();

                    }
                    else if (task.getResult().getDocuments().size()<=0)
                        showToast("Number Phone or Password is incorrect");
                    else
                        showToast("Login Fail");


                });

    }
    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
        if(preferencManager.getBool(KeyWord.KEY_IS_SET_PROFILE)){
            intent = new Intent(getApplicationContext(), ListActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void setListens() {
        binding.create.setOnClickListener(v -> startActivity(new Intent(Login.this,SignUp.class)));
        binding.btnSignIn.setOnClickListener(v -> {
            if(isVailed()){
                login();
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean isVailed() {
        if (binding.txtEmailSI.getText().toString().trim().isEmpty())
        {
            showToast("Enter Email");
            return false;
        }
        else if (binding.txtPassSI.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else
            return true;
    }

    private void loadActivity(Boolean load){
        if(load){
            binding.btnSignIn.setVisibility(View.INVISIBLE);

        }

        else {
            binding.btnSignIn.setVisibility(View.VISIBLE);
        }
    }

}