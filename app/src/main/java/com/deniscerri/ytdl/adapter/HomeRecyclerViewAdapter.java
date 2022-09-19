package com.deniscerri.ytdl.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deniscerri.ytdl.R;
import com.deniscerri.ytdl.database.Video;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Video> videoList;
    private ArrayList<MaterialCardView> checkedVideos;
    private final OnItemClickListener onItemClickListener;
    private Activity activity;

    public HomeRecyclerViewAdapter(ArrayList<Video> videos, OnItemClickListener onItemClickListener, Activity activity){
        this.videoList = videos;
        this.checkedVideos = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.result_card_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_card, parent, false);

        checkedVideos = new ArrayList<>();
        return new HomeRecyclerViewAdapter.ViewHolder(cardView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videoList.get(position);

        MaterialCardView card = holder.cardView;
        // THUMBNAIL ----------------------------------
        ImageView thumbnail = card.findViewById(R.id.result_image_view);
        String imageURL= video.getThumb();

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> Picasso.get().load(imageURL).into(thumbnail));
        thumbnail.setColorFilter(Color.argb(70, 0, 0, 0));

        // TITLE  ----------------------------------
        TextView videoTitle = card.findViewById(R.id.result_title);
        String title = video.getTitle();

        if(title.length() > 100){
            title = title.substring(0, 40) + "...";
        }
        videoTitle.setText(title);

        // Bottom Info ----------------------------------

        TextView bottomInfo = card.findViewById(R.id.result_info_bottom);
        String info = video.getAuthor() + " • " + video.getDuration();
        bottomInfo.setText(info);

        // BUTTONS ----------------------------------
        String videoID = video.getVideoId();

        LinearLayout buttonLayout = card.findViewById(R.id.download_button_layout);

        MaterialButton musicBtn = buttonLayout.findViewById(R.id.download_music);
        musicBtn.setTag(videoID + "##mp3");
        musicBtn.setOnClickListener(view -> onItemClickListener.onButtonClick(position, "mp3"));

        MaterialButton videoBtn = buttonLayout.findViewById(R.id.download_video);
        videoBtn.setTag(videoID + "##mp4");
        videoBtn.setOnClickListener(view -> onItemClickListener.onButtonClick(position, "mp4"));


        // PROGRESS BAR ----------------------------------------------------

        ProgressBar progressBar = card.findViewById(R.id.download_progress);
        progressBar.setVisibility(View.GONE);
        progressBar.setTag(videoID + "##progress");

        if(video.isAudioDownloaded() == 1){
            musicBtn.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_music_downloaded));
        }
        if(video.isVideoDownloaded() == 1){
            videoBtn.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_video_downloaded));
        }
        card.setChecked(false);
        card.setStrokeWidth(0);
        card.setTag(videoID + "##card");
        card.setOnLongClickListener(view -> {
            checkCard(card, position);
            return true;
        });
        card.setOnClickListener(view -> {
            if(checkedVideos.size() > 0){
                checkCard(card, position);
            }
        });
    }

    private void checkCard(MaterialCardView card, int position){
        if(card.isChecked()){
            card.setStrokeWidth(0);
            checkedVideos.remove(card);
        }else{
            card.setStrokeWidth(5);
            checkedVideos.add(card);
        }
        card.setChecked(!card.isChecked());
        onItemClickListener.onCardClick(position, card.isChecked());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public interface OnItemClickListener {
        void onButtonClick(int position, String type);
        void onCardClick(int position, boolean add);
    }

    public void setVideoList(ArrayList<Video> list, boolean reset){
        int size = videoList.size();
        if(reset || size == 0) clear();
        videoList.addAll(list);
        notifyItemRangeInserted(size, videoList.size());
    }

    public void clear(){
        int size = videoList.size();
        videoList = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

}