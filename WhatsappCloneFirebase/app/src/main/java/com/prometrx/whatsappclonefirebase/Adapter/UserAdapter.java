package com.prometrx.whatsappclonefirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prometrx.whatsappclonefirebase.MessageActivity;
import com.prometrx.whatsappclonefirebase.Model.Users;
import com.prometrx.whatsappclonefirebase.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<Users> usersList;
    private Context context;

    public UserAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.users_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

        Users users = usersList.get(position);

        holder.usernameText.setText(users.getUsername());
        if(users.getImageUrl().equals("default")) {
            holder.userImage.setImageResource(R.drawable.register_gradient);

        }else{
            Glide.with(context).load(users.getImageUrl()).into(holder.userImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MessageActivity.class);

                intent.putExtra("id", users.getUserid());
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usersItemText);
            userImage    = itemView.findViewById(R.id.usersItemImage);
        }

    }

}
