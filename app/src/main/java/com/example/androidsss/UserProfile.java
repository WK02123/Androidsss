package com.example.androidsss;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imageViewProfile;
    private EditText editTextName, editTextAddress, editTextPhone, editTextAge, editTextPassword;
    private Button buttonUploadImage, buttonSave;

    // Firebase Database reference
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Change this to your layout file

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSave = findViewById(R.id.buttonSave);

        // Set onClick listener for the upload button
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Set onClick listener for the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    // Method to open the gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageViewProfile.setImageURI(data.getData());
        }
    }

    // Method to save the profile data to Firebase Realtime Database
    private void saveProfile() {
        String name = editTextName.getText().toString();
        String address = editTextAddress.getText().toString();
        String phone = editTextPhone.getText().toString();
        String age = editTextAge.getText().toString();
        String newPassword = editTextPassword.getText().toString();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || age.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("UserProfile", "No user is logged in");
            return;
        }

        String userId = currentUser.getUid();
        User user = new User(name, address, phone, age, newPassword);

        // Update the profile data in Realtime Database
        databaseReference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("UserProfile", "Profile saved successfully for user ID: " + userId);

                        // Update the authentication password
                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(passwordUpdateTask -> {
                                    if (passwordUpdateTask.isSuccessful()) {
                                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("UserProfile", "Password updated successfully for user ID: " + userId);
                                    } else {
                                        Toast.makeText(this, "Failed to update password: " + passwordUpdateTask.getException(), Toast.LENGTH_SHORT).show();
                                        Log.e("UserProfile", "Error updating password: " + passwordUpdateTask.getException());
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to save profile. Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("UserProfile", "Error saving profile: " + task.getException());
                    }
                });
    }





    // User model class
    public static class User {
        public String name;
        public String address;
        public String phone;
        public String age;
        public String password;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String name, String address, String phone, String age, String password) {
            this.name = name;
            this.address = address;
            this.phone = phone;
            this.age = age;
            this.password = password;
        }
    }
}
