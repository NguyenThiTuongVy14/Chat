package com.example.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.R;
import com.example.chat.databinding.ActivityFogotPassBinding;
import com.example.chat.firebase.AuthService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ForgotPass extends AppCompatActivity{
    private EditText edtPhoneNumber;
    private TextView returnLogin;
    private Button btnConfirm;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fogot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtPhoneNumber = findViewById(R.id.txtPhone);
        btnConfirm = findViewById(R.id.btnConfirm);
        returnLogin=findViewById(R.id.returnLogin);

        btnConfirm.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                edtPhoneNumber.setError("Phone number invalid");
                edtPhoneNumber.requestFocus();
                return;
            }

            checkSdt(phoneNumber);
        });

        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ForgotPass.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkSdt(String phonenumber){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(KeyWord.KEY_COLECTION_USER).whereEqualTo(KeyWord.KEY_PHONE,phonenumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult()!=null&&task.isSuccessful()&&task.getResult().size()>0){
                    Intent intent = new Intent(ForgotPass.this, SetNewPass.class);
                    intent.putExtra("phone", phonenumber);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(ForgotPass.this, "Phone number don't exists", Toast.LENGTH_SHORT).show();
                }
            }
        }) .addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}




