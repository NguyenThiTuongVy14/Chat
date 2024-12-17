package com.example.chat.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.ImageProcessing;
import com.example.chat.Model.Location;
import com.example.chat.Model.Message;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.play.integrity.internal.l;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private static final int TYPE_MAP_SENDER = 2;
    private static final int TYPE_MAP_RECEIVER = 3;

    private List<Message> messageList;
    private String idSender;
    private Bitmap base64ImageReciver;
    private Bitmap base64ImageSender;
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timestampText;
        public ImageView imgUser;

        public SentMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.tvMessage);
            timestampText = view.findViewById(R.id.tvTimestamp);
            imgUser = view.findViewById(R.id.imgAvatar);
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
            imgUser = view.findViewById(R.id.imgAvatar);
        }
    }

    // ViewHolder for Location Messages
    public static class LocationMessageSendViewHolder extends RecyclerView.ViewHolder {
        public ImageView mapView;
        public ImageView avt;

        public LocationMessageSendViewHolder(View view) {
            super(view);
            mapView = view.findViewById(R.id.map); // map fragment view
            avt=view.findViewById(R.id.imageAVT);
        }
    }
    public static class LocationMessageRecieveViewHolder extends RecyclerView.ViewHolder {
        public ImageView mapView;
        public ImageView avt;

        public LocationMessageRecieveViewHolder(View view) {
            super(view);
            mapView = view.findViewById(R.id.map); // map fragment view
            avt=view.findViewById(R.id.imageAVT);
        }
    }

    // Constructor for MessageAdapter
    public MessageAdapter(List<Message> messageList, String idSender, Bitmap base64ImageReciver, Bitmap base64ImageSender) {
        this.messageList = messageList;
        this.idSender = idSender;
        this.base64ImageReciver = base64ImageReciver;
        this.base64ImageSender = base64ImageSender;
    }

    // Determine the view type (sent, received, or map)
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isMessLocation()) {
            if (message.getId_Sender().equals(idSender)){
                return TYPE_MAP_SENDER;
            }
            else return TYPE_MAP_RECEIVER;
        }
        if (message.getId_Sender().equals(idSender)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    // Create ViewHolder based on view type
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_SENT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sender_message, parent, false);
            return new SentMessageViewHolder(itemView);
        } else if (viewType == TYPE_RECEIVED) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recive_message, parent, false);
            return new ReceivedMessageViewHolder(itemView);
        } else if (viewType == TYPE_MAP_SENDER) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_map_sender, parent, false);
            return new LocationMessageSendViewHolder(itemView);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_map_recieve, parent, false);
            return new LocationMessageRecieveViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.messageText.setText(message.getMessage());
            sentHolder.timestampText.setText(getStringTimeStamp(message.getTimestamp()));
            if (base64ImageSender != null) {
                sentHolder.imgUser.setImageBitmap(base64ImageSender);
            } else {
                sentHolder.imgUser.setImageResource(R.drawable.img);
            }

        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.messageText.setText(message.getMessage());
            receivedHolder.timestampText.setText(getStringTimeStamp(message.getTimestamp()));
            if (base64ImageReciver != null) {
                receivedHolder.imgUser.setImageBitmap(base64ImageReciver);
            } else {
                receivedHolder.imgUser.setImageResource(R.drawable.img);
            }

        } else if (holder instanceof LocationMessageSendViewHolder) {
            LocationMessageSendViewHolder locationHolder = (LocationMessageSendViewHolder) holder;
            locationHolder.avt.setImageBitmap(base64ImageSender);
            locationHolder.mapView.setOnClickListener(v -> {
                if (listener != null) {

                    listener.onItemClick(null);
                }
            });
        } else if (holder instanceof LocationMessageRecieveViewHolder) {
            LocationMessageRecieveViewHolder locationHolder = (LocationMessageRecieveViewHolder) holder;
            locationHolder.avt.setImageBitmap(base64ImageReciver);
            locationHolder.mapView.setOnClickListener(v -> {
                if (listener != null) {
                    Location location = new Location();
                    location.setLocationX(message.getLatitude());
                    location.setLocationY(message.getLongitude());
                    location.setImgMarker(ImageProcessing.bitmapToString(base64ImageReciver));
                    listener.onItemClick(location);
                }
            });
        }
    }

    private String getStringTimeStamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    // Get the number of messages in the list
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Add a new message to the list
    public void addMessage(Message message, int pos) {
        messageList.add(pos, message);
        notifyItemInserted(pos);
    }
}
