package com.example.asdteachingtool.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import com.example.asdteachingtool.listeners.PlayerListener;

public class AudioController {

	private static final String LOG_TAG = "AudioRecorder";
	private MediaRecorder recorder;
	private MediaPlayer player;
	private List<PlayerListener> playerListeners;
	private Context context;

	public AudioController(Context context) {
		this.context = context;
		playerListeners = new ArrayList<PlayerListener>();
	}

	public synchronized void play(int id) {
		startPlayer(MediaPlayer.create(context, id));
	}

	public synchronized void play(String file) {
		if (file != null) {
			startPlayer(MediaPlayer.create(context, Uri.parse(file)));
		}
	}

	private synchronized void startPlayer(MediaPlayer player) {
		if (isPlaying()) {
			stop();
		}

		this.player = player;

		if (player != null) {
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					AudioController.this.stop();
					List<PlayerListener> toRemove = new ArrayList<PlayerListener>();
					for (PlayerListener observer : playerListeners) {
						if (observer.playReachedEnd()) {
							toRemove.add(observer);
						}
					}
					for (PlayerListener observer : toRemove) {
						playerListeners.remove(observer);
					}
				}
			});
			player.start();
		}
	}

	public synchronized void record(String file) {
		if (isRecording()) {
			stop();
		}

		recorder = new MediaRecorder();

		try {
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setOutputFile(file);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		recorder.start();
	}

	public synchronized void stop() {
		if (isRecording()) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}

		if (isPlaying()) {
			player.release();
			player = null;
		}
	}

	public Boolean isPlaying() {
		return player != null;
	}

	public Boolean isRecording() {
		return recorder != null;
	}

	public synchronized void addPlayObserver(PlayerListener playerListener) {
		playerListeners.add(playerListener);
	}
}
