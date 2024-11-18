package com.example.chat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private Button btnAddFriend, btnSearch;
    private EditText edtInputSearch; // Sửa lại tên biến để chính xác
    private TextView resultName;
    private ListView lvFriend;
    private PreferencManager preferencManager;
    private FirebaseFirestore dbStore;
    private ArrayAdapter adt;
    private ArrayList<User> listFriend;
    private String numberPhone;
    private String curentPhone;

    public Friend_Fragment() {
        // Required empty public constructor
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        lvFriend=view.findViewById(R.id.lvFriend);
        btnAddFriend = view.findViewById(R.id.add_friend_button);
        btnSearch = view.findViewById(R.id.search_button);
        edtInputSearch = view.findViewById(R.id.search_edit_text); // Sửa tên biến
        resultName = view.findViewById(R.id.result_name);
        curentPhone=preferencManager.getString(KeyWord.KEY_PHONE);

        loadListFriend();
        btnSearch.setOnClickListener(v -> {
            searchUser();
        });

        btnAddFriend.setOnClickListener(v -> {
            Add();

        });

        return view;
    }


    private void Add(){
        String textBtn= btnAddFriend.getText().toString();
        HashMap<String,Object>friend= new HashMap<>();
        if (textBtn.equals("Add Friend"))
        {
            friend.clear();
            friend.put(KeyWord.KEY_PHONE+"user1",curentPhone);
            friend.put(KeyWord.KEY_PHONE+"user2",numberPhone);
            friend.put(KeyWord.KEY_IS_FRIEND,false);
            friend.put("requests",curentPhone);
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .add(friend)
                    .addOnCompleteListener(command -> {
                        btnAddFriend.setText("Cancle Requests");
                    })
                    .addOnFailureListener(e -> {
                    });
        }
        else if (textBtn.equals("Accept"))
        {
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
                            btnAddFriend.setText("Is Friend");

                        }

                    });
        }
        else {
            friend.clear();
            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                    .whereEqualTo(KeyWord.KEY_PHONE+"user1",curentPhone)
                    .whereEqualTo(KeyWord.KEY_PHONE+"user2",numberPhone)
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
    private User getUserByPhone(String phone){

        User user = new User();
        dbStore.collection(KeyWord.KEY_COLECTION_USER)
                .whereEqualTo(KeyWord.KEY_PHONE,phone)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0){
                        DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                        user.setNumberPhone(phone);
                        user.setName(doc.getString(KeyWord.KEY_FULL_NAME));
                    }
                });
        return user;
    }
    private void loadListFriend(){
        listFriend= new ArrayList<>();
        dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereEqualTo(KeyWord.KEY_PHONE+"user1", curentPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0){
                        for (DocumentSnapshot doc: task.getResult()) {
                            User user = getUserByPhone(doc.getString(KeyWord.KEY_PHONE+"user2"));
                            listFriend.add(user);
                        }

                    }
                });
        dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereEqualTo(KeyWord.KEY_PHONE+"user2", curentPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0){
                        for (DocumentSnapshot doc: task.getResult()) {
                            User user = getUserByPhone(doc.getString(KeyWord.KEY_PHONE+"user1"));
                            listFriend.add(user);
                        }
                    }
                });

//        User a= new User();
//        a.setName("Job Baby");
//        a.setNumberPhone("0925458910");
//        listFriend.add(a);
        adt=new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,listFriend);
        lvFriend.setAdapter(adt);
        adt.notifyDataSetChanged();
    }
    private void searchUser() {
        numberPhone = edtInputSearch.getText().toString();
        if (numberPhone.length() == 10) {
            dbStore.collection(KeyWord.KEY_COLECTION_USER)
                    .whereEqualTo(KeyWord.KEY_PHONE, numberPhone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            String fullName = doc.getString(KeyWord.KEY_FULL_NAME);
                            btnAddFriend.setVisibility(View.VISIBLE);
                            if (fullName == null || fullName.isEmpty()) {
                                resultName.setText(numberPhone);
                            } else {
                                resultName.setText(fullName+"\n("+numberPhone+")");
                            }
                            dbStore.collection(KeyWord.KEY_COLECTION_FRIEND)
                                    .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                                    .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                                    .get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()&&task2.getResult()!=null && task2.getResult().getDocuments().size()>0) {
                                            DocumentSnapshot doc2 = task2.getResult().getDocuments().get(0);
                                            Boolean isFriend = doc2.getBoolean(KeyWord.KEY_IS_FRIEND);
                                            if (isFriend!=null && isFriend) {
                                                btnAddFriend.setText("Is Friend");
                                            } else {
                                                String userRequests = doc2.getString("requests");
                                                if (userRequests.equals(curentPhone)) {
                                                    btnAddFriend.setText("Cancle Requests");
                                                } else {
                                                    btnAddFriend.setText("Accept");
                                                }
                                            }
                                        }
                                        else
                                            btnAddFriend.setText("Add Friend");
                                    });

                        }
                        else {
                            resultName.setText(numberPhone +" not found");
                            btnAddFriend.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            edtInputSearch.setError("Invailed");
        }
    }
}
