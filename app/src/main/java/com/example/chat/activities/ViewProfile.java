package com.example.chat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.Model.User;
import com.example.chat.R;

public class ViewProfile extends AppCompatActivity {
    private TextView tvname1, tvname2;
    private Button btnChat;
    private ImageView img, back_ic;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        addListeners();
    }
    private void addListeners(){
        btnChat.setOnClickListener(view -> {
            Intent intent= new Intent(this, ScreenChat.class);
            intent.putExtra("us",user);
            startActivity(intent);
        });
        back_ic.setOnClickListener(view -> onBackPressed());
    }
    private void init(){
        tvname1=findViewById(R.id.tvname);
        tvname2=findViewById(R.id.tvname2);
        img = findViewById(R.id.imgAVT);
        btnChat=findViewById(R.id.btnChat);
        back_ic=findViewById(R.id.backIc);

        Intent intent = getIntent();
        user= intent.getParcelableExtra("user");
        if (user!=null){
            tvname1.setText(user.getName());
            tvname2.setText(user.getName());
           if(user.getAvataImage()==null||user.getAvataImage().isEmpty()){
               img.setImageResource(R.drawable.img);
           }
           else
               img.setImageBitmap(base64ToBitmap(user.getAvataImage()));

        }



    }
    public static Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }





}