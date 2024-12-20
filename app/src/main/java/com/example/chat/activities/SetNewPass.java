package com.example.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.R;

import java.util.regex.Pattern;

public class SetNewPass extends AppCompatActivity {
    EditText txtPass,txtRePass;
    TextView returnLogin;
    Button btnContinue;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_new_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtPass=findViewById(R.id.txtPass);
        txtRePass=findViewById(R.id.txtRePass);
        btnContinue=findViewById(R.id.btnConfirm2);
        returnLogin=findViewById(R.id.returnLogin2);

        phoneNumber = getIntent().getStringExtra("phone");

        btnContinue.setOnClickListener(v -> {
            String newPassword = txtPass.getText().toString().trim();
            String confirmPassword = txtRePass.getText().toString().trim();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{6,}$";
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                btnContinue.setError("Please, enter Password!");
                return;
            }
            if(!Pattern.matches(regex,newPassword)){
                txtPass.setError("Password invalid");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                txtRePass.setError("Incorrect");
                return;
            }

            Intent intent = new Intent(SetNewPass.this, VerifyCode.class);
            intent.putExtra("phone", phoneNumber);
            intent.putExtra("newPassword", newPassword);
            intent.putExtra("isForgotPassword",true);
            startActivity(intent);
        });
        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SetNewPass.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}