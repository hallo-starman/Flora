package com.asg.florafauna;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class PersonalRecordingsActivity extends AppCompatActivity {
    private ImageView selectedImage;
    private Bitmap currentImage;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL_STORAGE = 0;
    private String dirName = "FloraFauna/Recordings";
    private final File recordings = new File(Environment.getExternalStorageDirectory().toString(), dirName);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(ThemeCreator.getTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recordings);
        if (getSupportActionBar() != null) {
            FloraFaunaActionBar.createActionBar(getSupportActionBar(), R.layout.ab_recordings);
        }

        selectedImage = findViewById(R.id.imageView1);
        FloatingActionButton openGallery = findViewById(R.id.floatingUpload);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        // Open the default camera app to take a picture
        ImageButton openCamera = findViewById(R.id.floatingCameraButton);
        openCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });

        // Create directory for recordings
        // Request for permission to write to storage
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL_STORAGE);
        }

        // Checks if the recordings directory exists
        if (!recordings.exists()) {
            // If directory creation fails, tell the user
            if (!recordings.mkdirs()) {
                Log.d("error", "failed to make dir");
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_LONG).show();
            }
        }
        // If the directory exists, log error
        else {
            Log.d("Error", "Directory already exists");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recordings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent search_intent = new Intent(PersonalRecordingsActivity.this, SearchActivity.class);
                startActivity(search_intent);
                return true;
            case R.id.action_settings:
                Intent settings_intent = new Intent(PersonalRecordingsActivity.this, SettingsActivity.class);
                startActivity(settings_intent);
                return true;
            case R.id.action_help:
                Intent help_intent = new Intent(PersonalRecordingsActivity.this, HelpActivity.class);
                startActivity(help_intent);
                return true;
            case R.id.action_map:
                Intent intent = new Intent(PersonalRecordingsActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                // Code for images will be here
                try {
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    selectedImage.setImageBitmap(currentImage); //set the image view to the current image
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                }
                else {
                    // Permission denied, Unable to create directory
                }
            }
        }
    }

    public void goBack(View view){
        // Closes the activity
        setResult(RESULT_OK, null);
        finish();
    }
}

