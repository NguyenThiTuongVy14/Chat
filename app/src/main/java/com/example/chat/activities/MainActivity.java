package com.example.chat.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    PreferencManager preferencManager;
    ArrayAdapter<User> adtUser;
    List<User> listFrriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencManager = new PreferencManager(getApplicationContext());
        listFrriend= new ArrayList<User>();
        adtUser= new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1,listFrriend);
        binding.listView.setAdapter(adtUser);
        setLisener();
    }

    private void setLisener() {
        binding.btnSearch.setOnClickListener(v -> {
            FirebaseFirestore db= FirebaseFirestore.getInstance();
            db.collection(KeyWord.KEY_COLECTION_USER)
                    .whereEqualTo(KeyWord.KEY_PHONE, binding.edtSearch.getText().toString())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()&&task.getResult()!=null && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                            User user= new User();
                            user.setNumberPhone(doc.get(KeyWord.KEY_PHONE).toString());
                            user.setNumberPhone("User 1");
                            user.setAvataImage(null);
                            listFrriend.add(user);
                            adtUser.notifyDataSetChanged();
                            Toast.makeText(this, " có user", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(this, "Không có user", Toast.LENGTH_SHORT).show();


                    });
        });



    }


}