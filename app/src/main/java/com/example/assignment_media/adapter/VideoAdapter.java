package com.example.assignment_media.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment_media.R;
import com.example.assignment_media.model.VideoModel;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<VideoModel> videoList;
    private OnVideoClickListener listener;
    private Context context;


    public interface OnVideoClickListener {
        void onVideoClick(String videoPath);
    }

    public VideoAdapter(Context context,List<VideoModel> videoList, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.title.setText(video.getVideoTitle());
        holder.duration.setText(formatDuration(video.getVideoDuration()));
        Glide.with(context)
                .asBitmap()
                .load(video.getVideoPath()) // Load from file path
                .apply(new RequestOptions().frame(1000000)) // Extract frame at 1s
                .placeholder(R.drawable.ic_placeholder_video)
                .error(R.drawable.ic_placeholder_video)
                .into(holder.thumbnail);
        holder.itemView.setOnClickListener(v ->
                listener.onVideoClick(video.getVideoPath())
        );
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView title,duration;
        ImageView thumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            duration = itemView.findViewById(R.id.videoDuration);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(long durationMillis) {
        int hours = (int) (durationMillis / 1000) / 3600;
        int minutes = (int) ((durationMillis / 1000) % 3600) / 60;
        int seconds = (int) (durationMillis / 1000) % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

}
