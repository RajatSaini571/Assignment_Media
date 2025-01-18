package com.example.assignment_media.model;

public class VideoModel {
    private String videoPath;
    private String videoTitle;
    private long videoDuration;

    public VideoModel(String videoPath, String videoTitle, long videoDuration) {
        this.videoPath = videoPath;
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
    }

    public String getVideoPath() { return videoPath; }
    public String getVideoTitle() { return videoTitle; }
    public long getVideoDuration() { return videoDuration; }
}
