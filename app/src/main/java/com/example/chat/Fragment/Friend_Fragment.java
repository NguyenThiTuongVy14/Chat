package com.example.chat.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Adapter.AdapterUser;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.example.chat.activities.EditProfileActivity;
import com.example.chat.activities.ViewProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Friend_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Friend_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Button btnAddFriend;
    private EditText edtInputSearch;
    private TextView resultName,tvListFriend,tvlistReq,tvlistInvatition;
    private RecyclerView lvFriend;
    private PreferencManager preferencManager;
    private FirebaseFirestore dbStore;
    private AdapterUser adt;
    public static ArrayList<User> list;

    private String numberPhone;
    private String curentPhone;
    private LinearLayout layoutResult;
    private ImageView imgResultSearch;
    private int chooseList;
    private User curentUserSeacrh;
    public Friend_Fragment() {
    }

    public static Friend_Fragment newInstance(String param1, String param2) {
        Friend_Fragment fragment = new Friend_Fragment();
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
        dbStore = FirebaseFirestore.getInstance();
        preferencManager=new PreferencManager(getActivity());
        curentPhone=preferencManager.getString(KeyWord.KEY_PHONE);
        chooseList=1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        lvFriend=view.findViewById(R.id.lvFriend);
        layoutResult=view.findViewById(R.id.search_result_layout);
        btnAddFriend = view.findViewById(R.id.add_friend_button);
        edtInputSearch = view.findViewById(R.id.search_edit_text); // Sửa tên biến
        resultName = view.findViewById(R.id.result_name);
        imgResultSearch= view.findViewById(R.id.imageSearchUser);
        tvListFriend=view.findViewById(R.id.tvlistFriend);
        tvlistInvatition=view.findViewById(R.id.tvlistInvatition);
        tvlistReq=view.findViewById(R.id.tvlistReq);
        curentPhone=preferencManager.getString(KeyWord.KEY_PHONE);
        layoutResult.setVisibility(View.GONE);
        curentUserSeacrh=new User();

        loadFriend();

        adt.setOnItemClickListener(user -> {
            Intent intent = new Intent(getContext(), ViewProfile.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
        });
        tvlistReq.setOnClickListener(v -> {
            if(chooseList!=2)
                loadReq();

        });
        tvlistInvatition.setOnClickListener(v -> {
            if(chooseList!=3)
                loadInvation();

        });
        tvListFriend.setOnClickListener(v -> {
            if(chooseList!=1)
                loadFriend();
        });

        btnAddFriend.setOnClickListener(v -> {
            Log.i("loaiCurent", "onCreateView: "+curentUserSeacrh.getLoai());

            AddFriendProcessing();
            if (chooseList==curentUserSeacrh.getLoai()) {
                list.add(curentUserSeacrh);
                adt.notifyDataSetChanged();
            }

        });
//        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                if (i >= 0 && i < adt.getCount()&&chooseList==1) {
//                    Intent intent = new Intent(getContext(), ViewProfile.class);
//                    intent.putExtra("user",list.get(i));
//                    startActivity(intent);
//                }
//            }
//        });
        edtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtInputSearch.getText().toString().isEmpty()){
                    layoutResult.setVisibility(View.GONE);
                }
                else {
                    searchUser();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
    private void AddFriendProcessing(){
        String textBtn= btnAddFriend.getText().toString();
        HashMap<String,Object>friend= new HashMap<>();
        if (textBtn.equals("Add Friend"))
        {
            curentUserSeacrh.setLoai(2);
            friend.clear();
            friend.put(KeyWord.KEY_PHONE+"user1",curentPhone);
            friend.put(KeyWord.KEY_PHONE+"user2",numberPhone);
            friend.put(KeyWord.KEY_IS_FRIEND,false);
            friend.put("requests",curentPhone);
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .add(friend)
                    .addOnCompleteListener(command -> {
                        btnAddFriend.setText("Cancle");

                    })
                    .addOnFailureListener(e -> {
                    });
        }
        else if (textBtn.equals("Accept"))
        {
            curentUserSeacrh.setLoai(1);
            friend.clear();
            friend.put(KeyWord.KEY_IS_FRIEND,true);
            friend.put("requests",numberPhone);
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                    .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()&&task.getResult()!=null&&!task.getResult().isEmpty()){
                            DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                            DocumentReference docRef =doc.getReference();
                            docRef.update(friend);
                            btnAddFriend.setText("UnFriend");

                        }

                    });
        }
        else if (textBtn.equals("UnFriend")){
            friend.clear();
            curentUserSeacrh.setLoai(0);
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                    .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()&&task.getResult()!=null&&!task.getResult().isEmpty()){
                            DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                            DocumentReference docRef =doc.getReference();
                            docRef.delete();
                            btnAddFriend.setText("Add Friend");
                        }
                    });
        }
        else {
            curentUserSeacrh.setLoai(0);
            friend.clear();
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                    .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()&&task.getResult()!=null&&!task.getResult().isEmpty()){
                            DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                            DocumentReference docRef =doc.getReference();
                            docRef.delete();
                            btnAddFriend.setText("Add Friend");
                        }
                    });
        }

}
    private CompletableFuture<User> getUser(String phone, int loai) {
        CompletableFuture<User> future = new CompletableFuture<>();

        dbStore.collection(KeyWord.KEY_COLECTION_USER)
                .whereEqualTo(KeyWord.KEY_PHONE, phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (checkExists(doc.getString(KeyWord.KEY_PHONE))){
                            future.complete(null);
                        }
                        else{
                            User us = new User();
                            String base64Image =doc.getString("image");
                            us.setAvataImage(base64Image);
                            us.setLoai(loai);
                            us.setNumberPhone(phone);
                            us.setName(doc.getString(KeyWord.KEY_FULL_NAME));
                            us.setId(doc.getId());
                            future.complete(us);

                        }

                    } else {
                        future.complete(null);  // Kết thúc với kết quả null nếu không tìm thấy người dùng
                    }
                });

        return future;
    }

    private void show(String mess){
        Toast.makeText(getActivity(), mess, Toast.LENGTH_SHORT).show();
    }
    private boolean checkExists(String numberPhone){
        for (User us:list) {
            if(us.getNumberPhone().equals(numberPhone))
                return true;
        }
        return false;
    }
    private void loadInvation() {
        chooseList=3;
        tvlistInvatition.setTextColor(Color.YELLOW);
        tvlistReq.setTextColor(Color.WHITE);
        tvListFriend.setTextColor(Color.WHITE);
        list= new ArrayList<>();
        adt = new AdapterUser(list, preferencManager.getString(KeyWord.KEY_PHONE),KeyWord.TYPE_USER);
        lvFriend.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvFriend.setAdapter(adt);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user1", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user2");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    String req=doc.getString("requests");
                                    if (isFriend != null && !isFriend && req.equals(phoneFriend)) {
                                        getUser(phoneFriend, 3).thenAccept(user -> {
                                            if (user != null&&chooseList==3) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user2", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user1");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    String req=doc.getString("requests");
                                    if (isFriend != null && !isFriend && req.equals(phoneFriend)) {
                                        getUser(phoneFriend, 3).thenAccept(user -> {
                                            if (user != null&&chooseList==3) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        futures.add(task1);
        futures.add(task2);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    getActivity().runOnUiThread(() -> {
                        adt.notifyDataSetChanged();

                    });
                });
    }
    private void loadReq() {
        tvlistReq.setTextColor(Color.YELLOW);
        tvlistInvatition.setTextColor(Color.WHITE);
        tvListFriend.setTextColor(Color.WHITE);
        chooseList=2;
        list= new ArrayList<>();
        adt = new AdapterUser(list, preferencManager.getString(KeyWord.KEY_PHONE),KeyWord.TYPE_USER);
        lvFriend.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvFriend.setAdapter(adt);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user1", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user2");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    String req=doc.getString("requests");
                                    if (isFriend != null && !isFriend && req.equals(curentPhone)) {
                                        getUser(phoneFriend, 2).thenAccept(user -> {
                                            if (user != null&&chooseList==2) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user2", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user1");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    String req=doc.getString("requests");
                                    if (isFriend != null && !isFriend && req.equals(curentPhone)) {
                                        getUser(phoneFriend, 2).thenAccept(user -> {
                                            if (user != null&&chooseList==2) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        futures.add(task1);
        futures.add(task2);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    getActivity().runOnUiThread(() -> {
                        adt.notifyDataSetChanged();
                    });
                });
    }
    private void loadFriend() {
        tvlistInvatition.setTextColor(Color.WHITE);
        tvlistReq.setTextColor(Color.WHITE);
        tvListFriend.setTextColor(Color.YELLOW);
        chooseList=1;
        list= new ArrayList<>();
        adt = new AdapterUser(list, preferencManager.getString(KeyWord.KEY_PHONE),KeyWord.TYPE_USER);
        lvFriend.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvFriend.setAdapter(adt);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user1", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user2");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    if (isFriend != null && isFriend) {
                                        getUser(phoneFriend,1).thenAccept(user -> {
                                            if (user != null&&chooseList==1) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();// Thêm dữ liệu vào danh sách
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE + "user2", curentPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot qr = task.getResult();
                            if (!qr.isEmpty()) {
                                for (DocumentSnapshot doc : qr) {
                                    String phoneFriend = doc.getString(KeyWord.KEY_PHONE + "user1");
                                    Boolean isFriend = doc.getBoolean(KeyWord.KEY_IS_FRIEND);
                                    if (isFriend != null && isFriend) {
                                        getUser(phoneFriend, 1).thenAccept(user -> {
                                            if (user != null&&chooseList==1) {
                                                list.add(user);
                                                adt.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        });

        futures.add(task1);
        futures.add(task2);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    getActivity().runOnUiThread(() -> {
                    });
                });
}
    private void searchUser() {
        numberPhone = edtInputSearch.getText().toString();
        if (numberPhone.length() == 10) {
            layoutResult.setVisibility(View.VISIBLE);
            dbStore.collection(KeyWord.KEY_COLECTION_USER)
                    .whereEqualTo(KeyWord.KEY_PHONE, numberPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (numberPhone.equals(curentPhone)){
                            btnAddFriend.setVisibility(View.INVISIBLE);
                        }
                        else {
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        imgResultSearch.setVisibility(View.VISIBLE);
                        layoutResult.setVisibility(View.VISIBLE);
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            String fullName = doc.getString(KeyWord.KEY_FULL_NAME);
                            String base64Image = doc.getString("image");

                            if (base64Image != null && !base64Image.isEmpty()) {
                                imgResultSearch.setImageBitmap(base64ToBitmap(base64Image));
                                curentUserSeacrh.setAvataImage(base64Image);
                            } else {
                                imgResultSearch.setImageResource(R.drawable.img);
                                curentUserSeacrh.setAvataImage(null);
                            }
                            if (fullName == null || fullName.isEmpty()) {
                                resultName.setText(numberPhone);
                            } else {
                                resultName.setText(fullName+"\n("+numberPhone+")");
                            }
                            curentUserSeacrh.setNumberPhone(numberPhone);
                            curentUserSeacrh.setName(fullName);


                            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                                    .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                                    .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                                    .get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()&&task2.getResult()!=null && task2.getResult().getDocuments().size()>0) {
                                            DocumentSnapshot doc2 = task2.getResult().getDocuments().get(0);
                                            Boolean isFriend = doc2.getBoolean(KeyWord.KEY_IS_FRIEND);

                                            if (isFriend!=null && isFriend) {
                                                btnAddFriend.setText("UnFriend");

                                            } else {
                                                String userRequests = doc2.getString("requests");
                                                if (userRequests!=null){
                                                    if (userRequests.equals(curentPhone)) {
                                                        btnAddFriend.setText("Cancle");

                                                    } else {
                                                        btnAddFriend.setText("Accept");
                                                        curentUserSeacrh.setLoai(3);

                                                    }
                                                }
                                                else {

                                                    btnAddFriend.setText("Add Friend");
                                                }

                                            }
                                        }
                                        else

                                            btnAddFriend.setText("Add Friend");
                                    });

                        }
                        else {
                            imgResultSearch.setVisibility(View.GONE);
                            resultName.setText(numberPhone +" not found");
                            btnAddFriend.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            edtInputSearch.setError("Invailed");
        }
        layoutResult.setVisibility(View.GONE);

    }


    private Bitmap base64ToBitmap(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
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
