package com.example.asdteachingtool.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.asdteachingtool.observers.PlayObserver;

public class AudioController {

	private static final String LOG_TAG = "AudioRecorder";
	private MediaRecorder recorder;
	private MediaPlayer player;
	private List<PlayObserver> playObservers;
	private Context context;

	public AudioController(Context context) {
		this.context = context;
		playObservers = new ArrayList<PlayObserver>();
	}

	public void play(int id) {
		startPlayer(MediaPlayer.create(context, id));
	}

	public void play(String file) {
		if (isPlaying()) {
			stop();
		}

		player = new MediaPlayer();
		try {
			player.setDataSource(file);
			player.prepare();
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					AudioController.this.stop();
					for (PlayObserver observer : playObservers) {
						observer.playReachedEnd();
					}
				}
			});
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}
	
	private void startPlayer(MediaPlayer player) {
		if (isPlaying()) {
			stop();
		}
		
		this.player = player;

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				AudioController.this.stop();
				for (PlayObserver observer : playObservers) {
					observer.playReachedEnd();
				}
			}
		});		
	}

	public void record(String file) {
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

	public void stop() {
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

	public void addPlayObserver(PlayObserver playObserver) {
		playObservers.add(playObserver);
	}

}
