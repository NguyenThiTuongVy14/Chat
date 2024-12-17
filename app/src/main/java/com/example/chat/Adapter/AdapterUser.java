package com.example.chat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.ImageProcessing;
import com.example.chat.KEYWORD.KeyWord;
import com.example.chat.Model.Message;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> userList;
    private String currentPhone;
    private int type;
    private FirebaseFirestore db;
    private OnItemClickListener listener;
    private OnItemClickButtonListener listenerbutton;

    public AdapterUser(List<User> userList, String currentPhone, int type) {
        this.userList = userList;
        this.currentPhone = currentPhone;
        this.type=type;
        this.db = FirebaseFirestore.getInstance();
    }


    @Override
    public int getItemViewType(int position) {
        return type;
    }
    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    public interface OnItemClickButtonListener {
        void onItemClick(String phone, String text);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_mess, parent, false);
            return new UesrMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = userList.get(position);

        if(holder instanceof UserViewHolder){
            ((UserViewHolder)holder).tvInfo.setText(user.getName());
            if (user.getAvataImage() != null && !user.getAvataImage().isEmpty()) {
                ((UserViewHolder)holder).img.setImageBitmap(base64ToBitmap(user.getAvataImage()));
            } else {
                ((UserViewHolder)holder).img.setImageResource(R.drawable.img);
            }
            int type = user.getLoai();
            if (type == 1) {
                ((UserViewHolder)holder).btn.setText("UnFriend");
            } else if (type == 2) {
                ((UserViewHolder)holder).btn.setText("Cancel");
            } else {
                ((UserViewHolder)holder).btn.setText("Accept");
            }
            ((UserViewHolder)holder).btn.setOnClickListener(v -> {
                if (type == 1 || type == 2) {
                    unFriendOrUnReq(user.getNumberPhone(), position);
                } else {
                    accept(user.getNumberPhone(), position);
                }
            });
            ((UserViewHolder)holder).btn.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(user);
                }
            });
            ((UserViewHolder)holder).main.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(user);
                }
            });

        }
        if (holder instanceof UesrMessageViewHolder){
            ((UesrMessageViewHolder)holder).name.setText(user.getName());
            Date dateSentnewMess=user.getNewMess().getTimestamp().toDate();
            long milisec= new Date().getTime() - dateSentnewMess.getTime();

            ((UesrMessageViewHolder)holder).timestampText.setText(getStringTime(milisec));
            ((UesrMessageViewHolder)holder).newMessage.setText(user.getNewMess().getMessage());
            Bitmap bm = ImageProcessing.base64ToBitmap(user.getAvataImage());
            if(bm!=null){
                ((UesrMessageViewHolder)holder).imgUser.setImageBitmap(bm);
            }
            else {
                ((UesrMessageViewHolder) holder).imgUser.setImageResource(R.drawable.img);
            }
            ((UesrMessageViewHolder)holder).main.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(user);
                }
            });
        }


    }
    private String getStringTime(long milisec){
        String timestamp="";
        long s = milisec/1000;
        if(s<60){
            timestamp=s+ " sec ago";
        } else
        {
            long m= s/60;
            if(m<60){
                timestamp=m+" minute ago";
            }
            else{
                long h = m/60;
                if(h<24){
                    timestamp=m+" hour ago";
                }
                else{
                    long day = h/24;
                    timestamp=day+ " day ago";
                }
            }

        }
        return timestamp;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public  void addUser(User us){
        userList.add(us);
        notifyItemInserted(getItemCount()-1);
    }

    private Bitmap base64ToBitmap(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void accept(String numberPhone, int pos) {
        HashMap<String, Object> friend = new HashMap<>();
        friend.put(KeyWord.KEY_IS_FRIEND, true);
        friend.put("requests", numberPhone);

        db.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereIn(KeyWord.KEY_PHONE + "user1", Arrays.asList(currentPhone, numberPhone))
                .whereIn(KeyWord.KEY_PHONE + "user2", Arrays.asList(currentPhone, numberPhone))
                .get()
                .addOnCompleteListener(command -> {
                    if (command.isSuccessful() && command.getResult() != null && !command.getResult().isEmpty()) {
                        DocumentSnapshot doc = command.getResult().getDocuments().get(0);
                        DocumentReference docRef = doc.getReference();
                        docRef.update(friend);
                        userList.remove(pos);
                        notifyItemRemoved(pos);
                    }
                });
    }

    private void unFriendOrUnReq(String numberPhone, int pos) {
        db.collection(KeyWord.KEY_COLECTION_FRIEND)
                .whereIn(KeyWord.KEY_PHONE + "user1", Arrays.asList(currentPhone, numberPhone))
                .whereIn(KeyWord.KEY_PHONE + "user2", Arrays.asList(currentPhone, numberPhone))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        DocumentReference docRef = doc.getReference();
                        docRef.delete();
                        userList.remove(pos);
                        notifyItemRemoved(pos);
                    }
                });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView tvInfo;
        public Button btn;
        public View main;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgItemUser);
            tvInfo = itemView.findViewById(R.id.infoItemUser);
            btn = itemView.findViewById(R.id.btnItemUser);
            main = itemView.findViewById(R.id.mainUser);
        }
    }
    public static class UesrMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView newMessage;
        public TextView timestampText;
        public ImageView imgUser;
        public TextView name;
        public View main;

        public UesrMessageViewHolder(View view) {
            super(view);
            name =view.findViewById(R.id.tv_user_name);
            newMessage = view.findViewById(R.id.tv_last_message);
            timestampText = view.findViewById(R.id.tv_last_active);
            imgUser= view.findViewById(R.id.img_user_avatar);
            main = view.findViewById(R.id.main);

        }
    }
}
