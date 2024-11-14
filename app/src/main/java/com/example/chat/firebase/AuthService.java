package com.example.chat.firebase;

import android.app.Activity;
import android.util.Log;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class AuthService {

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private AuthCallback callback;

    public AuthService(AuthCallback callback) {
        this.mAuth = FirebaseAuth.getInstance();
        this.callback = callback;
    }

    public void sendOtp(String phoneNumber, Activity activity) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("OTP Verification", "Verification failed: " + e.getMessage());
                        callback.onVerificationFailed(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        mVerificationId = verificationId;
                        mResendToken = token;
                        Log.d("OTP Verification", "Code sent: " + verificationId);
                        callback.onCodeSent(verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(String otpCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        callback.onSignInSuccess(user);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            callback.onSignInFailed("Invalid OTP");
                        }
                    }
                });
    }

    public interface AuthCallback {
        void onCodeSent(String verificationId);
        void onVerificationFailed(String errorMessage);
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailed(String errorMessage);
    }
}
