package com.example.chat.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.databinding.ActivityLoginBinding;
import com.example.chat.firebase.NotificationSender;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.Key;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    PreferencManager preferencManager;
    DocumentSnapshot doc;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        preferencManager = new PreferencManager(getApplicationContext());
        if(preferencManager.getBool(KeyWord.KEY_IS_LOGIN)){
            startMainActivity();
        }
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListens();
        checkPermission();

        
    }
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Quyền thông báo
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM", "Notification permission granted");
            } else {
                Log.d("FCM", "Notification permission denied");
                Toast.makeText(this, "Notification permission is required to receive notifications", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Network", "Network permissions granted");
            } else {

                Toast.makeText(this, "Network permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login(){

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
                        getFMC_Token();


                    }
                    else if (task.getResult().getDocuments().size()<=0)
                        showToast("Number Phone or Password is incorrect");
                    else
                        showToast("Login Fail");


                });

    }
    private void getFMC_Token(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token=task.getResult();
                        HashMap<String,Object>updateUser= new HashMap<>();
                        updateUser.put(KeyWord.KEY_FMC_TOKEN,token);
                        db.collection(KeyWord.KEY_COLECTION_USER)
                                .document(preferencManager.getString(KeyWord.KEY_USERID))
                                .update(updateUser)
                                .addOnCompleteListener(taskUpdateUser -> {
                                    if(taskUpdateUser.isSuccessful()){
                                        NotificationSender.sendNotification(this,token,"New login","login in viet nam",null);
                                        startMainActivity();
                                    }
                                    else
                                        Toast.makeText(this, "Error Login", Toast.LENGTH_SHORT).show();
                                });

                    }

                });
    }
    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
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