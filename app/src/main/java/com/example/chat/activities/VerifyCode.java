package com.example.chat.activities;

import static com.example.chat.KEYWORD.KeyWord.KEY_COLECTION_USER;
import static com.example.chat.KEYWORD.KeyWord.KEY_FMC_TOKEN;
import static com.example.chat.KEYWORD.KeyWord.KEY_FULL_NAME;
import static com.example.chat.KEYWORD.KeyWord.KEY_IMAGE;
import static com.example.chat.KEYWORD.KeyWord.KEY_IS_SET_PROFILE;
import static com.example.chat.KEYWORD.KeyWord.KEY_PASS;
import static com.example.chat.KEYWORD.KeyWord.KEY_PHONE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.HassVerifyPass;
import com.example.chat.R;
import com.example.chat.activities.Login;
import com.example.chat.firebase.AuthService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class VerifyCode extends AppCompatActivity implements AuthService.AuthCallback {

    private EditText codeDigit1, codeDigit2, codeDigit3, codeDigit4,codeDigit5,codeDigit6;
    private Button verifyButton;
    private AuthService authService;
    private String phone,newPass;
    Boolean flag;

    private Intent reIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        authService = new AuthService(this);


        codeDigit1 = findViewById(R.id.code_digit_1);
        codeDigit2 = findViewById(R.id.code_digit_2);
        codeDigit3 = findViewById(R.id.code_digit_3);
        codeDigit4 = findViewById(R.id.code_digit_4);
        codeDigit5 = findViewById(R.id.code_digit_5);
        codeDigit6 = findViewById(R.id.code_digit_6);

        verifyButton = findViewById(R.id.verify_button);

        setUpOTPInputListeners();
        reIntent = getIntent();

        phone=reIntent.getStringExtra("phone");
        flag=reIntent.getBooleanExtra("isForgotPassword",false);
        newPass=reIntent.getStringExtra("newPassword");
        newPass= HassVerifyPass.hashPassword(newPass);

        authService.sendOtp(formatNumberPhone(phone),this);
        verifyButton.setOnClickListener(view -> {
            String otpCode = codeDigit1.getText().toString()
                    + codeDigit2.getText().toString()
                    + codeDigit3.getText().toString()
                    + codeDigit4.getText().toString()
                    +codeDigit5.getText().toString()
                    +codeDigit6.getText().toString();

            if (otpCode.length() == 6) {
                authService.verifyOtp(otpCode);
            } else {
                showToast("Please enter a valid OTP");
            }
        });
    }
    private String formatNumberPhone(String number){
        if(number.length()==10 && number.charAt(0)=='0')
            number="+84"+number.substring(1);
        return number;
    }
    private void setUpOTPInputListeners() {
        codeDigit1.addTextChangedListener(new OTPTextWatcher(null,codeDigit1, codeDigit2));
        codeDigit2.addTextChangedListener(new OTPTextWatcher(codeDigit1,codeDigit2, codeDigit3));
        codeDigit3.addTextChangedListener(new OTPTextWatcher(codeDigit2,codeDigit3, codeDigit4));
        codeDigit4.addTextChangedListener(new OTPTextWatcher(codeDigit3,codeDigit4, codeDigit5));
        codeDigit5.addTextChangedListener(new OTPTextWatcher(codeDigit4,codeDigit5, codeDigit6));
        codeDigit6.addTextChangedListener(new OTPTextWatcher(codeDigit5,codeDigit6, null));

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCodeSent(String verificationId) {
    }

    @Override
    public void onVerificationFailed(String errorMessage) {
        showToast("Verification Failed: " + errorMessage);
    }

    @Override
    public void onSignInSuccess(FirebaseUser user) {
        if (flag){
            updatePass();
        }
        else {
            signUp();
        }
    }

    private void updatePass() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(KEY_COLECTION_USER).whereEqualTo(KEY_PHONE,phone).get().addOnCompleteListener(task -> {
            if (task.getResult()!=null&&task.isSuccessful()&&task.getResult().size()>0){
                String documentId = task.getResult().getDocuments().get(0).getId();

                db.collection(KEY_COLECTION_USER)
                        .document(documentId)
                        .update(KEY_PASS, newPass)
                        .addOnSuccessListener(aVoid -> {
                            showToast("Password updated successfully!");
                            Intent intent = new Intent(VerifyCode.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            showToast("Failed to update password: " + e.getMessage());
                        });
            } else {
                showToast("Phone number not found!");
            }
                })
                .addOnFailureListener(e -> {
                    showToast("Error occurred: " + e.getMessage());
                });
    }

    @Override
    public void onSignInFailed(String errorMessage) {
        showToast("Sign In Failed: " + errorMessage);
    }

    private void signUp() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String password = reIntent.getStringExtra("password");

        db.collection(KEY_COLECTION_USER)
                .whereEqualTo(KEY_PHONE, phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                        HashMap<String, Object> user = new HashMap<>();
                        user.put(KEY_PHONE, phone);
                        user.put(KEY_PASS, HassVerifyPass.hashPassword(password));
                        user.put(KEY_FULL_NAME,"");
                        user.put("image","");
                        user.put(KEY_IS_SET_PROFILE,false);
                        user.put(KEY_FMC_TOKEN,"");
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


    private static class OTPTextWatcher implements android.text.TextWatcher {
        private final EditText preEditText,currentEditText, nextEditText;

        public OTPTextWatcher(EditText preEditText, EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
            this.preEditText = preEditText;
            currentEditText.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN && keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                    if (currentEditText.getText().length() == 0) {
                        if (currentEditText.hasFocus()) {
                            currentEditText.clearFocus();
                            if (currentEditText != null) {
                                preEditText.requestFocus();
                            }
                        }
                    }
                }
                return false;
            });
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (currentEditText.getText().length() == 1) {
                if (nextEditText != null) {
                    nextEditText.setText("");
                    nextEditText.requestFocus();
                }

            }
        }

        @Override
        public void afterTextChanged(android.text.Editable editable) {
        }
    }
}