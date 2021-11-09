package com.prometrx.whatsappclonefirebase.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.whatsappclonefirebase.Adapter.UserAdapter;
import com.prometrx.whatsappclonefirebase.Model.Users;
import com.prometrx.whatsappclonefirebase.R;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> usersList;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        usersList = new ArrayList<>();

        readUsers();
        return view;
    }

    private void readUsers() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference cr = firebaseFirestore.collection("MyUsers");

        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    QuerySnapshot querySnapshot = task.getResult();

                    usersList.clear();

                    for(QueryDocumentSnapshot qs : querySnapshot){

                        Users usersX = qs.toObject(Users.class);
                        usersX.setUserid(qs.getId());
                        if(!qs.getId().equals(firebaseUser.getUid())) {
                            usersList.add(usersX);

                        }

                        userAdapter = new UserAdapter(usersList, requireActivity());
                        recyclerView.setAdapter(userAdapter);

                    }

                }else{
                    Toast.makeText(requireActivity(), "Task is not successful", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}