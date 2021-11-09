package com.prometrx.whatsappclonefirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.whatsappclonefirebase.Adapter.MessageAdapter;
import com.prometrx.whatsappclonefirebase.Model.Chat;
import com.prometrx.whatsappclonefirebase.Model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ImageView profileImage;
    private TextView profileUserText;
    private Intent intent;
    private ImageButton sendButton;
    private EditText sendText;
    private RecyclerView recyclerView;
    private String userid;
    private List<Chat> chatList;
    private MessageAdapter messageAdapter;
    private Users refreshUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();

        intent = getIntent();
        userid = intent.getStringExtra("id");

        DocumentReference documentReference = firebaseFirestore.collection("MyUsers").document(userid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {


                    DocumentSnapshot documentSnapshot = task.getResult();
                    Users usersMessageProfile = documentSnapshot.toObject(Users.class);

                    refreshUsers = usersMessageProfile;
                    profileUserText.setText(usersMessageProfile.getUsername());

                    if(usersMessageProfile.getImageUrl().equals("default")) {
                        profileImage.setImageResource(R.drawable.ic_launcher_background);
                    }else{
                        Glide.with(MessageActivity.this).load(usersMessageProfile.getImageUrl()).into(profileImage);
                    }

                    readMessages(firebaseUser.getUid(), userid, refreshUsers.getImageUrl());

                }
                else{
                    Toast.makeText(MessageActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = sendText.getText().toString();
                if(msg.equals("")) {
                    //Empty
                }
                else{
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                sendText.setText("");



            }
        });

    }

    private void sendMessage(String sender,String receiver,String message) {

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("date",FieldValue.serverTimestamp());

        firebaseFirestore.collection("Chats").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {

                    //readMessage(firebaseUser.getUid(), userid, refreshUsers.getImageUrl());

                }else{
                    Toast.makeText(MessageActivity.this,"Error Send null", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String,Object> hashMapList = new HashMap<>();
        hashMapList.put("sender", sender);
        hashMapList.put("imageUrl",refreshUsers.getImageUrl());
        hashMapList.put("userName", refreshUsers.getUsername());
        hashMapList.put("receiver", receiver);
        //hashMapList.put("date",FieldValue.serverTimestamp());

        firebaseFirestore.collection("Chatlist").add(hashMapList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {



            }
        });

    }


    /*
    private void readMessage(String myId,String userId,String imageUrl) {

        chatList = new ArrayList<>();

        firebaseFirestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    chatList.clear();

                    for (QueryDocumentSnapshot qs:task.getResult()) {

                        Chat chat = qs.toObject(Chat.class);

                        if((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) || (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                            chatList.add(chat);
                        }

                        messageAdapter = new MessageAdapter(chatList, MessageActivity.this, imageUrl);
                        recyclerView.setAdapter(messageAdapter);

                    }

                    //messageAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(MessageActivity.this, "Error! readMessage", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
*/


    private void readMessages(String myId,String userId,String imageUrl){
        chatList = new ArrayList<>();

        firebaseFirestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null) {
                    Toast.makeText(MessageActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value != null) {

                    chatList.clear();

                    for(QueryDocumentSnapshot queryDocumentSnapshot : value) {

                        Chat chat = queryDocumentSnapshot.toObject(Chat.class);

                        if((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) || (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                            chatList.add(chat);
                        }

                        messageAdapter = new MessageAdapter(chatList, MessageActivity.this, imageUrl);
                        recyclerView.setAdapter(messageAdapter);

                    }

                    messageAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MessageActivity.this, "Error! readMessage", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.profileImage);
        profileUserText = findViewById(R.id.profileUserText);

        sendButton   = findViewById(R.id.send_btn);
        sendText     = findViewById(R.id.editText_send);
        recyclerView = findViewById(R.id.old_messageRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

}