package com.example.chat.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.chat.firebase.NotificationSender;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ScreenChat extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvName;
    private ImageButton btnsend;
    private ImageView shareLocation;
    private ImageButton btn_back;
    private EditText edtMessage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore db;
    private String chatRoomId = null;
    private String idSender;
    private String idReciver;
    PreferencManager preferencManager;
    private boolean isLoading = false;
    private boolean isFirstLoadComplete = false; // Flag for first load completion
    private User us = null;

    // Location-related fields
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_chat);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        preferencManager = new PreferencManager(this);
        tvName = findViewById(R.id.tvName);
        edtMessage = findViewById(R.id.etMessage);
        btnsend = findViewById(R.id.btnSend);
        btn_back = findViewById(R.id.back);
        shareLocation = findViewById(R.id.shareLoction);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.processbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        us = getIntent().getParcelableExtra("us");

        if (us != null) {
            tvName.setText(us.getName());
            idSender = preferencManager.getString(KeyWord.KEY_USERID);
            idReciver = us.getId();
            messageAdapter = new MessageAdapter(new ArrayList<>(), idSender,
                    ImageProcessing.base64ToBitmap(us.getAvataImage()),
                    ImageProcessing.base64ToBitmap(preferencManager.getString("image")));

            recyclerView.setAdapter(messageAdapter);


            if (idSender.compareTo(idReciver) > 0) {
                chatRoomId = idSender + idReciver;
            } else {
                chatRoomId = idReciver + idSender;
            }
        }

        listenForMessages();
        firstLoad();
        messageAdapter.setOnItemClickListener(location -> {
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("location",location);
            startActivity(intent);

        });
        btn_back.setOnClickListener(v -> onBackPressed());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFirstLoadComplete && !recyclerView.canScrollVertically(-1) && !isLoading) {
                    loadMore();
                }
            }
        });
        shareLocation.setOnClickListener(v -> {
            String userName = us.getName();
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_builder, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView)
                    .setCancelable(false);

            TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
            TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
            Button btnOk = dialogView.findViewById(R.id.btnOk);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            dialogTitle.setText("Share Location");
            dialogMessage.setText("Are you sure you want to share your location with " + userName + "?");
            AlertDialog dialog = builder.create();
            dialog.show();
            btnOk.setOnClickListener(v1 -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    sendLocationMessage();
                }
                dialog.dismiss();
            });


            btnCancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();

        });
        btnsend.setOnClickListener(view -> {
            if (chatRoomId != null && !edtMessage.getText().toString().isEmpty()) {
                Message message = new Message();
                message.setMessage(edtMessage.getText().toString());
                message.setId_Sender(idSender);
                message.setId_Receive(idReciver);
                message.setMessLocation(false);
                message.setTimestamp(new Timestamp(new Date()));
                sendMessage(message);
                edtMessage.setText("");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocationMessage();
            } else {
                Toast.makeText(this, "Location permission is required to share location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void sendLocationMessage() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Message message = new Message();
                        message.setId_Sender(idSender);
                        message.setId_Receive(idReciver);
                        message.setMessLocation(true);
                        message.setLatitude(location.getLatitude());
                        message.setLongitude(location.getLongitude());
                        message.setTimestamp(new Timestamp(new Date()));
                        sendMessage(message);
                    } else {
                        Toast.makeText(this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(this, "Error getting location.", Toast.LENGTH_SHORT).show());
    }

    private void sendMessage(Message message) {
        db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                .document(chatRoomId)
                .collection("chat")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    HashMap<String, Object> newMess = new HashMap<>();
                    newMess.put("newMess", message.getMessage());
                    newMess.put("time", message.getTimestamp());
                    db.collection(KeyWord.KEY_COLECTION_MESSAGE)
                            .document(chatRoomId)
                            .set(newMess);
                    NotificationSender.sendNotification(this, us.getFmc_token(), "New message", us.getName() + ": " + message.getMessage(), null);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show();
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
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    isLoading = false;
                    Toast.makeText(this, "Error loading messages.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setProgressBar() {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
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
                            messageAdapter.addMessage(message, 0);
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
