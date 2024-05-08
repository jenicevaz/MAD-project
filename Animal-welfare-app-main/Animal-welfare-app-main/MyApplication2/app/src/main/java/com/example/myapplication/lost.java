package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class lost extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageRef;

    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private Spinner animalSpinner;
    private EditText breedEditText;
    private EditText colorEditText;
    private Button uploadButton;
    private Button captureImageButton;
    private ImageView animalImageView;
    private DatabaseReference databaseRef;
    private Uri imageUri;
    private EditText searchEditText;
    private ListView animalListView;
    private ArrayAdapter<String> animalListAdapter;
    private List<String> animalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        initializeFirebase();
        initializeViews();

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_STORAGE_PERMISSION);
        }
    }

    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("animals");
        storageRef = FirebaseStorage.getInstance().getReference("animal_images");
    }

    private void initializeViews() {
        animalSpinner = findViewById(R.id.animal_spinner);
        breedEditText = findViewById(R.id.editTextBreedSearch);
        colorEditText = findViewById(R.id.editTextColorSearch);
        uploadButton = findViewById(R.id.upload_button);
        captureImageButton = findViewById(R.id.capture_image_button);
        animalImageView = findViewById(R.id.animal_image_view);
        searchEditText = findViewById(R.id.search_edit_text);
        animalListView = findViewById(R.id.animal_list_view);

        ArrayAdapter<CharSequence> animalAdapter = ArrayAdapter.createFromResource(
                this, R.array.animal_array, android.R.layout.simple_spinner_item);
        animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSpinner.setAdapter(animalAdapter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadAnimalData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(lost.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(lost.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(lost.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                } else {
                    captureImage();
                }
            }
        });

        animalList = new ArrayList<>();
        animalListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, animalList);
        animalListView.setAdapter(animalListAdapter);

        searchEditText = findViewById(R.id.search_edit_text);
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String breedSearch = breedEditText.getText().toString().trim();
                String colorSearch = colorEditText.getText().toString().trim();
                searchData(breedSearch, colorSearch);
            }
        });
    }

    private void extractImageUrl(String animalType, String breed) {
        DatabaseReference animalRef = databaseRef.child(animalType);
        animalRef.orderByChild("breed").equalTo(breed).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Animal animal = snapshot.getValue(Animal.class);
                    if (animal != null) {
                        String imageUrl = animal.getImage();
                        if (imageUrl != null) {
                            loadImage(imageUrl);
                        } else {
                            Toast.makeText(lost.this, "Image URL is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(lost.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(String imageUrl) {
        storageRef.child(imageUrl).getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Picasso.get()
                    .load(downloadUrl)
                    .into(animalImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Failed to load image
                            Toast.makeText(lost.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
        }).addOnFailureListener(e -> {
            // Error getting the image URL
            Toast.makeText(lost.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void uploadAnimalData() throws IOException {
        String animalType = animalSpinner.getSelectedItem().toString();
        String breed = breedEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();

        if (breed.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference animalRef = databaseRef.child(animalType);
        DatabaseReference newAnimalRef = animalRef.push();
        newAnimalRef.child("breed").setValue(breed);
        newAnimalRef.child("color").setValue(color);

        if (imageUri != null) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            uploadImage(bitmap, newAnimalRef); // Pass the newAnimalRef to the uploadImage method
        }

        Toast.makeText(this, "Animal data uploaded successfully", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private void uploadImage(Bitmap bitmap, DatabaseReference newAnimalRef) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String imageName = "animal_" + System.currentTimeMillis() + ".jpg"; // Unique image name
        StorageReference imageRef = storageRef.child(imageName);

        imageRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                // Use the downloadUrl as needed
                // You can also store the downloadUrl in your database if required
                newAnimalRef.child("image").setValue(downloadUrl); // Set the download URL using the newAnimalRef
            });
        }).addOnFailureListener(e -> {
            // Error uploading the image
            Toast.makeText(lost.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void processCapturedImage(Intent data) {
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

        // Resize the image
        Bitmap resizedBitmap = resizeBitmap(imageBitmap, 800); // Adjust the desired width here

        // Set the resized image to the ImageView
        animalImageView.setImageBitmap(resizedBitmap);

        // Convert bitmap to URI
        imageUri = getImageUri(resizedBitmap);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int desiredWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) desiredWidth) / width;
        float scaleHeight = ((float) desiredWidth) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    private void searchData(String breedSearch, String colorSearch) {
        DatabaseReference animalRef = databaseRef.child(animalSpinner.getSelectedItem().toString());
        animalRef.orderByChild("breed").equalTo(breedSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                animalList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Animal animal = snapshot.getValue(Animal.class);
                    if (animal != null) {
                        String breed = animal.getBreed();
                        animalList.add(breed);
                    }
                }
                animalListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(lost.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        breedEditText.setText("");
        colorEditText.setText("");
        animalImageView.setImageResource(android.R.color.transparent);
        imageUri = null;
    }

    // Remove the duplicated onItemSelected() method

}
