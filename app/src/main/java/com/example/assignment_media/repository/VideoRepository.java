package com.example.assignment_media.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.example.assignment_media.model.VideoModel;
import java.util.ArrayList;
import java.util.List;

public class VideoRepository {
    public List<VideoModel> getAllVideos(Context context) {
        List<VideoModel> videoList = new ArrayList<>();
        String[] media = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                media, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                long duration = cursor.getLong(2);
                videoList.add(new VideoModel(path, title, duration));
            }
            cursor.close();
        }
        return videoList;
    }
}
