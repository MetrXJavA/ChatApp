package com.prometrx.whatsappclonefirebase.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.whatsappclonefirebase.Adapter.UserAdapter;
import com.prometrx.whatsappclonefirebase.Model.Chatlist;
import com.prometrx.whatsappclonefirebase.Model.Users;
import com.prometrx.whatsappclonefirebase.R;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private List<Chatlist> chatList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser myUser;
    private List<Users> usersList;
    private RecyclerView recyclerViewChats;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        recyclerViewChats.setHasFixedSize(true);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(requireActivity()));


        readChatlist();
        return view;
    }

    private void readChatlist() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        myUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        firebaseFirestore.collection("Chatlist").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value != null) {

                    usersList.clear();
                    List<String> usersid = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : value) {

                        Chatlist cl = queryDocumentSnapshot.toObject(Chatlist.class);

                        cl.setRecv(queryDocumentSnapshot.get("receiver").toString());
                        cl.setSend(queryDocumentSnapshot.get("sender").toString());

                        System.out.println(cl.getRecv().equals(myUser.getUid()) + "---");
                        System.out.println(cl.getSend().equals(myUser.getUid()) + "---");

                        if(cl.getRecv().equals(myUser.getUid())){
                            Users users = new Users(cl.getRecv(),cl.getImageUrl(),cl.getUserName());

                            if(!users.getUserid().contains(myUser.getUid())) {
                                if(!usersid.contains(users.getUserid())) {
                                    usersid.add(users.getUserid());
                                    usersList.add(users);
                                }
                            }

                        }

                        else if(cl.getSend().equals(myUser.getUid())){

                            Users users = new Users(cl.getRecv(),cl.getImageUrl(),cl.getUserName());

                            if(!users.getUserid().contains(myUser.getUid())) {
                                if(!usersid.contains(users.getUserid())) {
                                    usersid.add(users.getUserid());
                                    usersList.add(users);
                                }
                            }

                        }

                        UserAdapter userAdapter = new UserAdapter(usersList, requireActivity());
                        recyclerViewChats.setAdapter(userAdapter);

                    }



                }

            }
        });



    }

}