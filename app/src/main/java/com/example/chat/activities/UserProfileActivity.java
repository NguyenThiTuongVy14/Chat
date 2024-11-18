package com.example.chat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    EditText edtName;
    Button save;
    PreferencManager preferencManager;
    private ImageView imageView;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        edtName=findViewById(R.id.editTextName);
        save=findViewById(R.id.buttonSaveProfile);
        // Khởi tạo Firebase
        preferencManager=new PreferencManager(this);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.profileImageView);

        imageView.setOnClickListener(v -> openImageChooser());
        save.setOnClickListener(v -> {
            if(!edtName.getText().toString().isEmpty()){
                saveProfile(edtName.getText().toString());
            }
        });
    }
    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void saveProfile(String name) {
        HashMap<String, Object> user = new HashMap<>();
        user.put("fullName",name);
        user.put(KeyWord.KEY_IS_SET_PROFILE,true);
        db.collection(KeyWord.KEY_COLECTION_USER)
                .document(preferencManager.getString(KeyWord.KEY_USERID))
                .update(user)
                .addOnSuccessListener(s->{
                    startMainActivity();
                })
                .addOnFailureListener(f->{
                    Toast.makeText(this, "Error in save", Toast.LENGTH_SHORT).show();
                });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();


            Glide.with(this).load(imageUri).into(imageView);


            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = storageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {

                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    saveImageUrlToFirestore(imageUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }


    private void saveImageUrlToFirestore(String imageUrl) {

        db.collection("images").add(new ImageData(imageUrl))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ảnh đã được lưu!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public static class ImageData {
        private String imageUrl;

        public ImageData() {

        }

        public ImageData(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}