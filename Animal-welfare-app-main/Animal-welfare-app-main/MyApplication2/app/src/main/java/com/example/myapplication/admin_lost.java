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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class admin_lost extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageRef;

    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private Spinner animalSpinner;
    private EditText breedEditText;
    private EditText idEditText;
    private EditText colorEditText;
    private Button uploadButton;
    private Button captureImageButton;
    private ImageView animalImageView;
    private DatabaseReference databaseRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lost);
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
        breedEditText = findViewById(R.id.breed_edit_text);
        idEditText = findViewById(R.id.id_edit_text);
        colorEditText = findViewById(R.id.color_edit_text);
        uploadButton = findViewById(R.id.upload_button);
        captureImageButton = findViewById(R.id.capture_image_button);
        animalImageView = findViewById(R.id.animal_image_view);

        ArrayAdapter<CharSequence> animalAdapter = ArrayAdapter.createFromResource(
                this, R.array.animal_array, android.R.layout.simple_spinner_item);
        animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSpinner.setAdapter(animalAdapter);
        animalSpinner.setOnItemSelectedListener(this);

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
                if (ContextCompat.checkSelfPermission(admin_lost.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(admin_lost.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(admin_lost.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                } else {
                    captureImage();
                }
            }
        });
    }

    private void uploadAnimalData() throws IOException {
        String animalType = animalSpinner.getSelectedItem().toString();
        String breed = breedEditText.getText().toString().trim();
        String id = idEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();

        if (breed.isEmpty() || id.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference animalRef = databaseRef.child(animalType);
        DatabaseReference newAnimalRef = animalRef.push();
        newAnimalRef.child("breed").setValue(breed);
        newAnimalRef.child("id").setValue(id);
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
            Toast.makeText(admin_lost.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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

    private void clearForm() {
        breedEditText.getText().clear();
        idEditText.getText().clear();
        colorEditText.getText().clear();
        animalImageView.setImageDrawable(null);
        imageUri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processCapturedImage(data);
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "AnimalImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedAnimal = parent.getItemAtPosition(position).toString();
        if (selectedAnimal.equals("Dog")) {
            captureImageButton.setVisibility(View.VISIBLE);
        } else {
            captureImageButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle when nothing is selected in the spinner
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform the desired operation
                captureImage();
            } else {
                // Permission denied, show a message and guide the user to app settings
                Toast.makeText(this, "Storage permission denied. Please grant the permission from the app settings.", Toast.LENGTH_LONG).show();
                openAppSettings();
            }
        }
    }

    private void openAppSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        settingsIntent.setData(uri);
        startActivity(settingsIntent);
    }
}
