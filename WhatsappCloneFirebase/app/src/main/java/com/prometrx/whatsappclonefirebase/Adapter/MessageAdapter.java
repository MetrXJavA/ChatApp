package com.prometrx.whatsappclonefirebase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prometrx.whatsappclonefirebase.Model.Chat;
import com.prometrx.whatsappclonefirebase.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {

    private TextView showMsg;
    private ImageView profileImage;
    private List<Chat> chatList;
    private Context context;
    private String imageUrl;
    public static final int MSG_TYPE_LEFT  = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser fuser;


    public MessageAdapter(List<Chat> chatList, Context context, String imageUrl) {
        this.chatList = chatList;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_LEFT) {
            View view0 = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent,false);
            return new viewHolder(view0);
        }
        else{
            View view1 = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent,false);
            return new viewHolder(view1);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Chat chat = chatList.get(position);

        showMsg.setText(chat.getMessage());

        if(imageUrl.equals("default")) {
            profileImage.setImageResource(R.drawable.ic_launcher_background);
        }else{
            Glide.with(context).load(imageUrl).into(profileImage);
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            showMsg      =  itemView.findViewById(R.id.profile_textView);
            profileImage = itemView.findViewById(R.id.profile_imageView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if(chatList.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_LEFT;
        }
        else{
            return MSG_TYPE_RIGHT;
        }

    }
}
