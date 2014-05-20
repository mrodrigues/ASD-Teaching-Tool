package com.example.asdteachingtool.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.asdteachingtool.observers.PlayObserver;

public class AudioController {

	private static final String LOG_TAG = "AudioRecorder";
	private MediaRecorder recorder;
	private MediaPlayer player;
	private String file;
	private List<PlayObserver> playObservers;

	public AudioController() {
		playObservers = new ArrayList<PlayObserver>();
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

	public void record() {
		if (isRecording()) {
			stop();
		}
		
		recorder = new MediaRecorder();
		File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
				"/audio/");
		storageDir.mkdir();

		try {
			file = File.createTempFile("audio", ".3gp", storageDir).getPath();
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
	
	public String getOutputFile() {
		return file;
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
