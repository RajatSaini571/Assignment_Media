package com.example.assignment_media.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import com.example.assignment_media.R;
import com.example.assignment_media.adapter.VideoAdapter;
import com.example.assignment_media.viewmodel.VideoViewModel;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private VideoViewModel videoViewModel;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private TextView tv_noVideos;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadVideos();
                } else {
                    handlePermissionDenial();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("All Videos");
        }
        recyclerView = findViewById(R.id.recyclerView);
        tv_noVideos = findViewById(R.id.noVideosTextView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        checkPermissions();
    }

    private void checkPermissions() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // for Android 13+ (API 33+)
            permission = Manifest.permission.READ_MEDIA_VIDEO;
        } else { // Android 6.0 to Android 12 (API 24+)
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadVideos();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void handlePermissionDenial() {
        int denialCount = sharedPreferences.getInt("permission_denial_count", 0) + 1;
        sharedPreferences.edit().putInt("permission_denial_count", denialCount).apply();

        if (denialCount >= 2) {
            showPermissionDialog();
        } else {
            Toast.makeText(this, "Permission Denied! Please allow permission to access videos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Please provide permission to access media. You need to allow permission from app settings.")
                .setPositiveButton("Give Permission", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void loadVideos() {
        videoViewModel.getVideos().observe(this, videos -> {
            if (videos == null || videos.isEmpty()) {
                tv_noVideos.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tv_noVideos.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                VideoAdapter adapter = new VideoAdapter(this,videos, videoPath -> {
                    Uri videoUri = getContentUriFromFilePath(this, videoPath);
                    Intent intent = new Intent(this, VideoPlayerActivity.class);
                    intent.putExtra("VIDEO_PATH", videoUri.toString());
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            }
        });
        videoViewModel.fetchVideos();
    }

    public Uri getContentUriFromFilePath(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=?",
                new String[]{filePath},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        } else {
            return Uri.fromFile(new File(filePath)); // Fallback
        }
    }

}

