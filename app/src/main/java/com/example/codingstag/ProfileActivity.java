package com.example.codingstag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codingstag.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference userRef;

    ImageView userImage;
    TextView userEmail;
    EditText userName;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
//        userRef = database.getReference("users");
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("users").child(mUser.getUid());

        userImage = binding.profileImage;
        userEmail = binding.profileEmail;
        userName = binding.profileName;
        
        binding.buttonSave.setOnClickListener(view -> changeUserData());

        binding.profileImage.setOnClickListener(view -> selectImageIntent());

        binding.logoutBtn.setOnClickListener(view -> logout());

        binding.backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user data
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                assert imageUrl != null;
                if(!imageUrl.equals("none")) {
                    Picasso.get()
                            .load(imageUrl)
                            .into(userImage);
                }
                userEmail.setText(email);
                assert name != null;
                if(name.equals("abc")) name = "Enter your Name";
                userName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        Uri imageUri = intent.getData();
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userProfileImages/" + mUser.getUid() + "/" + imageUri.getLastPathSegment());
//        UploadTask uploadTask = storageRef.putFile(imageUri);
//        uploadTask.continueWithTask(task -> {
//            if (!task.isSuccessful()) {
//                throw Objects.requireNonNull(task.getException());
//            }
//            return storageRef.getDownloadUrl();
//        }).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Uri downloadUri = task.getResult();
//
//                // Update user's image URL in Firebase Database
//                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
//                userRef.child("imageUrl").setValue(downloadUri.toString());
//            }
//        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri imageUri = data.getData();
                        binding.profileImage.setImageURI(imageUri);
                    }
                }
            });

    private void changeUserData() {
        if (userImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();

            // Get a reference to Firebase Storage and create a unique file name for the image
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String fileName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child("images/" + fileName + ".jpg");

            // Convert the bitmap image to a byte array and upload it to Firebase Storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);

            // Show a progress dialog while the upload is in progress
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // After the upload is complete, get the download URL of the image and store it in Firebase Realtime Database
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // Store the image URL in Firebase Realtime Database
                            userRef.child("imageUrl")
                                    .setValue(imageUrl);

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        userRef.child("name").setValue(userName.getText().toString())
                .addOnSuccessListener(unused -> Toast.makeText(ProfileActivity.this, "User's name updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this,  "Error updating user's name." + e, Toast.LENGTH_SHORT).show());
    }

}