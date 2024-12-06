package com.example.chat.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Message_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Message_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PreferencManager preferencManager;
    private FirebaseFirestore db;
    List<User> listUser;
    ArrayAdapter<User> adtUser;
    public Message_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Message_Fragment newInstance(String param1, String param2) {
        Message_Fragment fragment = new Message_Fragment();
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
        db= FirebaseFirestore.getInstance();
        listUser= new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message, container, false);


        return view;
    }


    private void loadUserChat(){
        db.collection("message")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null){
                        List<DocumentSnapshot> docs= task.getResult().getDocuments();
                        for (DocumentSnapshot doc: docs) {
                            String idDocument=doc.getId();
                            String idUser=idDocument
                                    .replace(preferencManager.getString(KeyWord.KEY_USERID),"");


                        }
                    }
                });

    }

    private void getUserbyIduser(String idUser){
        User us = new User();
        db.collection(KeyWord.KEY_COLECTION_USER)
                .document(idUser)
                .get()
                .addOnCompleteListener(task -> {

                });
    }

}