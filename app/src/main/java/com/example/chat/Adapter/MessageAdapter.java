package com.example.chat.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Model.Message;
import com.example.chat.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;

    private List<Message> messageList;
    private String idSender;
    private Bitmap base64ImageReciver;
    private Bitmap base64ImageSender;

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timestampText;
        public ImageView imgUser;


        public SentMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.tvMessage);
            timestampText = view.findViewById(R.id.tvTimestamp);
            imgUser= view.findViewById(R.id.imgAvatar);


        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timestampText;
        public ImageView imgUser;
        public ReceivedMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.tvMessage);
            timestampText = view.findViewById(R.id.tvTimestamp);
            imgUser= view.findViewById(R.id.imgAvatar);

        }
    }

    public MessageAdapter(List<Message> messageList,String idSender, Bitmap base64ImageReciver, Bitmap base64ImageSender) {
        this.messageList = messageList;
        this.idSender=idSender;
        this.base64ImageReciver=base64ImageReciver;
        this.base64ImageSender=base64ImageSender;
    }
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getId_Sender().equals(idSender)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_SENT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sender_message, parent, false);
            return new SentMessageViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recive_message, parent, false);
            return new ReceivedMessageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).messageText.setText(message.getMessage());
            ((SentMessageViewHolder) holder).timestampText.setText(getStringTimeStamp(message.getTimestamp()));
            if (base64ImageSender!=null)
                ((SentMessageViewHolder) holder).imgUser.setImageBitmap(base64ImageSender);
            else
                ((SentMessageViewHolder) holder).imgUser.setImageResource(R.drawable.img);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).messageText.setText(message.getMessage());
            ((ReceivedMessageViewHolder) holder).timestampText.setText(getStringTimeStamp(message.getTimestamp()));
            if (base64ImageReciver!=null)
                ((ReceivedMessageViewHolder) holder).imgUser.setImageBitmap(base64ImageReciver);
            else
                ((ReceivedMessageViewHolder) holder).imgUser.setImageResource(R.drawable.img);

        }

    }
    private String getStringTimeStamp(Timestamp timestamp){
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
//    public void removeMessage(Message removedMessage) {
//        for (int i = 0; i < messageList.size(); i++) {
//            if (messageList.get(i).getId().equals(removedMessage.getId())) { // So sánh bằng ID
//                messageList.remove(i); // Xóa tin nhắn
//                notifyItemRemoved(i); // Cập nhật RecyclerView
//                break;
//            }
//        }
//    }


//        public void updateMessage(Message updatedMessage) {
//        for (int i = 0; i < messageList.size(); i++) {
//            if (messageList.get(i).getId().equals(updatedMessage.getId())) { // So sánh bằng ID
//                messageList.set(i, updatedMessage); // Cập nhật tin nhắn
//                notifyItemChanged(i); // Thông báo RecyclerView
//                break;
//            }
//        }
//    }
    public void addMessage(Message message,int pos) {
        messageList.add(pos,message);
        notifyItemInserted(pos);
    }
}
