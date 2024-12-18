package com.example.chat.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.ImageProcessing;
import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.example.chat.activities.Login;
import com.example.chat.firebase.MessageService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
    private ImageView imgProfile;
    private TextView tvName;
    private View layout_logout;
    private PreferencManager preferencManager;
    public Profile_Fragment() {

    }

    public static Profile_Fragment newInstance(String param1, String param2) {
        Profile_Fragment fragment = new Profile_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        preferencManager = new PreferencManager(getActivity());




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getView(inflater,container);
    }
    private View getView(LayoutInflater inflater, ViewGroup container){
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        imgProfile = v.findViewById(R.id.imgAVTProfile);
        tvName = v.findViewById(R.id.tvNamePageProfile);
        layout_logout=v.findViewById(R.id.logout);
        layout_logout.setOnClickListener(view -> {
            deleteFMC_TOKEN();
        });
        getInfo();
        return v;
    }

    private void getInfo() {
        tvName.setText(preferencManager.getString(KeyWord.KEY_FULL_NAME));
        String base64Image = preferencManager.getString("image");
        if (base64Image != null && !base64Image.isEmpty()) {
            imgProfile.setImageBitmap(ImageProcessing.base64ToBitmap(base64Image));
        } else {
            imgProfile.setImageResource(R.drawable.img);
        }
    }

    private void updateInfo() {
        DocumentReference docRef = dbStore.collection(KeyWord.KEY_COLECTION_USER).document(preferencManager.getString(KeyWord.KEY_USERID));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString(KeyWord.KEY_FULL_NAME);
                    String base64Image = document.getString("image");
                    String numberPhone =document.getString(KeyWord.KEY_PHONE);
                    tvName.setText(name+"\n"+numberPhone);

                    if (base64Image != null && !base64Image.isEmpty()) {
                        imgProfile.setImageBitmap(ImageProcessing.base64ToBitmap(base64Image));
                    } else {
                        imgProfile.setImageResource(R.drawable.img);
                    }
                }
            } else {
                tvName.setText("Failed to load data.");
            }
        });
    }
    private void logout(){
        preferencManager.clear();
        Intent intent = new Intent(getActivity(),Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void deleteFMC_TOKEN() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            logout();
        });
    }




}
