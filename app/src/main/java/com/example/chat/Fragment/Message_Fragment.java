package com.example.chat.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.Adapter.AdapterUser;
import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.Message;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.example.chat.activities.ScreenChat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private List<User> listUser;
    private AdapterUser adtUser;
    private RecyclerView recycleViewMessageUser;
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recycleViewMessageUser =view.findViewById(R.id.recyViewMessage);
        recycleViewMessageUser.setLayoutManager(new LinearLayoutManager(getActivity()));
//        listUser= new ArrayList<>();
//        adtUser=new AdapterUser(listUser,preferencManager.getString(KeyWord.KEY_PHONE),1);
//        recycleViewMessageUser.setAdapter(adtUser);
//        loadUserChat();
//        adtUser.setOnItemClickListener(user -> {
//            Intent intent = new Intent(getActivity(), ScreenChat.class);
//            intent.putExtra("us",user);
//            startActivity(intent);
//        });
        return view;
    }


    private void loadUserChat(){
        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null) {
                        for (DocumentSnapshot chatRoom : task.getResult()) {
                            String chatRoomID = chatRoom.getId();
                            String idUser = chatRoomID.replace(preferencManager.getString(KeyWord.KEY_USERID),"");
                            if(!idUser.equals(chatRoomID)) {
                                getUser(idUser).thenAccept(user -> {
                                    if (user != null) {
                                        Message newMess = new Message();
                                        newMess.setMessage(chatRoom.getString("newMess"));
                                        Timestamp time = chatRoom.getTimestamp("time");
                                        newMess.setTimestamp(time);
                                        user.setNewMess(newMess);
                                        adtUser.addUser(user);
                                    }
                                });
                            }
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "Loi", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private CompletableFuture<User> getUser(String idUser) {
        CompletableFuture<User> future = new CompletableFuture<>();
        db.collection(KeyWord.KEY_COLECTION_USER)
                .document(idUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        User us = new User();
                        String base64Image = doc.getString("image");
                        if (base64Image == null) base64Image = null;
                        us.setAvataImage(base64Image);
                        us.setNumberPhone(doc.getString(KeyWord.KEY_PHONE));
                        us.setName(doc.getString(KeyWord.KEY_FULL_NAME));
                        us.setId(doc.getId());
                        us.setFmc_token(doc.getString(KeyWord.KEY_FMC_TOKEN));
                        future.complete(us);
                    } else {
                        future.completeExceptionally(new Exception("Failed to get user data"));
                    }
                })
                .addOnFailureListener(e -> future.completeExceptionally(e));

        return future;
    }

    @Override
    public void onResume() {
        super.onResume();
        listUser= new ArrayList<>();
        adtUser=new AdapterUser(listUser,preferencManager.getString(KeyWord.KEY_PHONE),1);
        recycleViewMessageUser.setAdapter(adtUser);
        loadUserChat();
        adtUser.setOnItemClickListener(user -> {
            Intent intent = new Intent(getActivity(), ScreenChat.class);
            intent.putExtra("us",user);
            startActivity(intent);
        });
    }
}