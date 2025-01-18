package com.example.assignment_media.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.assignment_media.model.VideoModel;
import com.example.assignment_media.repository.VideoRepository;

import java.util.List;

public class VideoViewModel extends AndroidViewModel {
    private VideoRepository repository;
    private MutableLiveData<List<VideoModel>> videosLiveData;

    public VideoViewModel(Application application) {
        super(application);
        repository = new VideoRepository();
        videosLiveData = new MutableLiveData<>();
    }

    public void fetchVideos() {
        List<VideoModel> videos = repository.getAllVideos(getApplication());
        videosLiveData.setValue(videos);
    }

    public LiveData<List<VideoModel>> getVideos() {
        return videosLiveData;
    }
}
