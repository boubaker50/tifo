package com.thephoenixit.walidchaieb;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class songService extends Service {
    private MediaPlayer mMediaPlayer;
    private static final String TAG = "songService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String songUrl = intent.getStringExtra("song");
        Log.e(TAG, "onStartCommand: " + songUrl);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "Walid" + File.separator + songUrl.substring(songUrl.lastIndexOf('/') + 1));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
    }
}
