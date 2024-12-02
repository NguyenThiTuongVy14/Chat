package com.example.chat.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.User;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AdpterUser extends ArrayAdapter<User> {
    private Activity context;
    private int resource;
    private List<User> objects;
    private String curentPhone;
    private FirebaseFirestore db;


    public AdpterUser(@NonNull Activity context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        PreferencManager preferencManager= new PreferencManager(context);
        this.curentPhone=preferencManager.getString(KeyWord.KEY_PHONE);
        this.db=FirebaseFirestore.getInstance();
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(resource, null);
        }

        User user = objects.get(position);
        LinearLayout itemUser = view.findViewById(R.id.itemUser);
        ImageView img = view.findViewById(R.id.imgItemUser);
        TextView tvInfo = view.findViewById(R.id.infoItemUser);
        Button btn = view.findViewById(R.id.btnItemUser);
        tvInfo.setText(user.getName());
        if(user.getAvataImage()!=null && !user.getAvataImage().isEmpty())
            img.setImageBitmap(base64ToBitmap(user.getAvataImage()));
        else
            img.setImageResource(R.drawable.img);
        int type=user.getLoai();
        if (type==1){
            btn.setText("UnFriend");
            btn.setVisibility(View.GONE);
        }
        else if (type==2){
            btn.setText("Cancle Requests");
        }
        else {
            btn.setText("Accept");
        }
        btn.setOnClickListener(v -> {
            if (type==1){
                unFriendOrUnReq(user.getNumberPhone(),position);
            }
            else if (type==2){
                unFriendOrUnReq(user.getNumberPhone(),position);
            }
            else {
                accecpt(user.getNumberPhone(),position);
            }
        });
        return view;
    }
    private void accecpt(String numberPhone, int pos){
        HashMap<String,Object> friend= new HashMap<>();
        friend.put(KeyWord.KEY_IS_FRIEND,true);
        friend.put("requests",numberPhone);
        db.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                .get()
                .addOnCompleteListener(command -> {
                    if (command.isSuccessful()&&command.getResult()!=null&&!command.getResult().isEmpty()) {
                        DocumentSnapshot doc = command.getResult().getDocuments().get(0);
                        DocumentReference docRef = doc.getReference();
                        docRef.update(friend);
                        objects.remove(pos);
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {

                });

    }
    private void unFriendOrUnReq(String numberPhone, int pos){
        HashMap<String,Object> friend= new HashMap<>();
        friend.clear();
        Log.i("TAG UNfriend", "unFriend: 1"+numberPhone);
        db.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereIn(KeyWord.KEY_PHONE+"user1", Arrays.asList(curentPhone,numberPhone))
                .whereIn(KeyWord.KEY_PHONE+"user2",Arrays.asList(curentPhone,numberPhone))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null&&!task.getResult().isEmpty()){
                        DocumentSnapshot doc =task.getResult().getDocuments().get(0);
                        DocumentReference docRef =doc.getReference();
                        docRef.delete();
                        objects.remove(pos);
                        notifyDataSetChanged();
                    }
                });
    }


}
