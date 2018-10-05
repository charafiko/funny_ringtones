package com.acc.gp.ringtones.funny.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acc.gp.ringtones.funny.R;
import com.acc.gp.ringtones.funny.interfaces.ItemClickListener;
import com.acc.gp.ringtones.funny.model.Ringtone;

import java.io.IOException;
import java.util.ArrayList;

public class RingtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Ringtone> ringtoneList;
    private ItemClickListener mClickListener;
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private int oldPosition;

    public void onItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public RingtoneAdapter(Context mContext, ArrayList<Ringtone> ringtoneList) {
        this.mContext = mContext;
        this.ringtoneList = ringtoneList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ringtone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final Ringtone ringtone = ringtoneList.get(position);

        viewHolder.tvTitle.setText(ringtone.getName());
        viewHolder.tvDuration.setText(ringtone.getDuration());

        viewHolder.lnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(position);
            }
        });

        viewHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && oldPosition != position) {
                    killMediaPlayer();
                    mediaPlayer = null;
                    playbackPosition = 0;
                }
                if (mediaPlayer == null) {
                    oldPosition = position;
                    playAudio(ringtone.getRingtoneUrl(), viewHolder.imgPlay, viewHolder.circularProgressBar);
                    viewHolder.imgPlay.setImageResource(R.mipmap.ic_pause);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        viewHolder.imgPlay.setImageResource(R.mipmap.ic_play);
                    } else {
                        mediaPlayer.start();
                        viewHolder.imgPlay.setImageResource(R.mipmap.ic_pause);
                    }
                }
//                if (!playPause) {
//
//                    if (initialStage) {
//                        new Player().execute(ringtone.getRingtoneUrl());
//                    } else {
//                        if (!mediaPlayer.isPlaying()) mediaPlayer.start();
//                    }
//
//                    playPause = true;
//                } else {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                    }
//
//                    playPause = false;
//                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lnItem;
        ImageView imgPlay;
        TextView tvTitle;
        TextView tvDuration;
        ProgressBar circularProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            lnItem = itemView.findViewById(R.id.lnItem);
            imgPlay = itemView.findViewById(R.id.imgPlay);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            circularProgressBar = itemView.findViewById(R.id.circularProgressBar);
        }
    }

    private boolean initialStage = true;
    private boolean playPause;
    private ProgressDialog progressDialog;

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playPause = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepareAsync();
                prepared = true;
            } catch (Exception e) {
                Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }

    private void playAudio(String url, final ImageView imgPlay, final ProgressBar circularProgressBar) {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }

            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    initialStage = true;
                    playPause = false;
                    imgPlay.setImageResource(R.mipmap.ic_play);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            });


            final Handler mHandler = new Handler();
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        circularProgressBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playbackPosition = 0;
        }
    }

    private void restartMediaPlayer() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
        }
    }
}
