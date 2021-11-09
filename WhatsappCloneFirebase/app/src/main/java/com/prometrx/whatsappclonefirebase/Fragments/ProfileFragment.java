package com.prometrx.whatsappclonefirebase.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prometrx.whatsappclonefirebase.R;

import java.io.IOException;
import java.util.UUID;


public class ProfileFragment extends Fragment {

    private TextView userText, emailText;
    private Button saveButton;
    private ImageButton imageButton;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser fuser;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri imageUrl;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        imageButton = view.findViewById(R.id.profileImageSelectedButton);
        saveButton = view.findViewById(R.id.saveButton);
        userText = view.findViewById(R.id.profileUsernameTextView);
        emailText = view.findViewById(R.id.profileEmailTextView);

        firebaseFirestore.collection("MyUsers").document(fuser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {

                    userText.setText("Username: " + value.get("username"));
                    emailText.setText("E-mail: " + fuser.getEmail());

                    if(value.get("imageUrl").toString().equals("default")) {
                        imageButton.setImageResource(R.drawable.ic_launcher_background);
                    }else{
                        Uri uri = (Uri) value.get("imageUrl");

                        imageButton.setImageURI(uri);
                    }

                }


            }
        });

        requestLauncher();
        imageButton();
        saveButton();

        return view;
    }

    private void saveButton() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUrl != null) {
                    //UUID
                    UUID uuid = UUID.randomUUID();
                    final String imagerandomName = "images/" + uuid + ".jpg";

                    storageReference.child(imagerandomName).putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            StorageReference newStorageReference = FirebaseStorage.getInstance().getReference(imagerandomName);

                            newStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();

                                    firebaseFirestore.collection("MyUsers").document(fuser.getUid()).update("imageUrl", url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(requireActivity(), "Successful", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });






                }

            }
        });

    }

    private void imageButton() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //request Permission

                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        Snackbar.make(view, "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //request Permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();

                    } else {
                        //request Permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }

                } else {
                    //Go gallery
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);


                }

            }
        });
    }

    private void requestLauncher() {


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent intentG = result.getData();

                    if (intentG != null) {

                        imageUrl = intentG.getData();

                        Bitmap x;
                        try {
                            x = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUrl);
                            imageButton.setImageBitmap(x);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }

            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //go to gallery
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);


                } else {
                    //Permission denied
                    Toast.makeText(requireActivity(), "Permission needed for gallery", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}