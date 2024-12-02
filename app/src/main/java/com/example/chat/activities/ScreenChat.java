package com.example.chat.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.ImageProcessing;
import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.Message;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.units.qual.s;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScreenChat extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvName;
    private ImageButton btnsend;
    private EditText edtMessage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore db;
    private String chatRoomId=null;
    private String idSender;
    private String idReciver;
    PreferencManager preferencManager;
    private boolean isLoading=false;
    private boolean isFirstLoadComplete = false; // Flag for first load completion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        preferencManager= new PreferencManager(this);
        tvName=findViewById(R.id.tvName);
        edtMessage= findViewById(R.id.etMessage);
        btnsend=findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.processbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        User us = (User)getIntent().getSerializableExtra("us");
        if (us!=null){
            tvName.setText(us.getName());
            idSender=preferencManager.getString(KeyWord.KEY_USERID);
            idReciver=us.getId();

            messageAdapter = new MessageAdapter(new ArrayList<>(),idSender,
                    ImageProcessing.base64ToBitmap(us.getAvataImage()),
                    ImageProcessing.base64ToBitmap(preferencManager.getString("image")));
            recyclerView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
            if(idSender.compareTo(idReciver)>0){
                chatRoomId=idSender+idReciver;
            }
            else
                chatRoomId=idReciver+idSender;


        }

        listenForMessages();
        firstLoad();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFirstLoadComplete && !recyclerView.canScrollVertically(-1) && !isLoading) {
                    loadMore();
                    Toast.makeText(ScreenChat.this, "Loading more", Toast.LENGTH_SHORT).show();  // Show loading toast
                }
            }
        });


        btnsend.setOnClickListener(view -> {
            if (chatRoomId!=null&&!edtMessage.getText().toString().isEmpty()){
                Message message = new Message();
                message.setMessage(edtMessage.getText().toString());
                message.setId_Sender(idSender);
                message.setId_Receive(idReciver);
                message.setTimestamp(new Timestamp(new Date()));
                sendMessage(message);
                edtMessage.setText("");

            }
        });

    }



    private void sendMessage(Message message){
        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .document(chatRoomId)
                .collection("chat")
                .add(message)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    private static final int PAGE_SIZE = 10;
    private DocumentSnapshot lastVisible;

    private void firstLoad() {
        isLoading = true;
        setProgressBar();
        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .document(chatRoomId)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            messageAdapter.addMessage(message, 0);
                        }

                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    }
                    isLoading = false;
                    isFirstLoadComplete = true;
                    setProgressBar();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
//                    progressBar.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    isLoading = false;
                    Toast.makeText(this, "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
                });
    }
    private void setProgressBar(){
        if (isLoading){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    private void loadMore() {
        if (lastVisible == null || isLoading) return;
        isLoading = true;
        setProgressBar();

        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .document(chatRoomId)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            messageAdapter.addMessage(message, 0); // Thêm tin nhắn vào đầu
                        }
                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                    }
                    isLoading = false;
                    setProgressBar();
                })
                .addOnFailureListener(e -> {
                    isLoading = false;
                    Toast.makeText(this, "Error loading more messages", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isFirstLoad = true;

    private void listenForMessages() {
        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .document(chatRoomId)
                .collection("chat")
                .orderBy("timestamp")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot != null) {
                        if (!isFirstLoad) {
                            for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                                Message message = dc.getDocument().toObject(Message.class);
                                switch (dc.getType()) {
                                    case ADDED:
                                        int pos = messageAdapter.getItemCount();
                                        messageAdapter.addMessage(message, pos);
                                        recyclerView.smoothScrollToPosition(pos);
                                        break;
                                    case MODIFIED:
                                        break;
                                    case REMOVED:
                                        break;
                                }
                            }
                        } else {
                            isFirstLoad = false;
                        }

                        isLoading = false;
                    }
                });
    }


}





