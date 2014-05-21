package com.example.asdteachingtool.factories;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.asdteachingtool.R;
import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.observers.PlayObserver;

public class AudioControllerFactories {
	
	private static final String LOG_TAG = "AudioControllerFactories";
	private AudioController audioController;
	private Context context;

	public AudioControllerFactories(Context context) {
		this.context = context;
		this.audioController = new AudioController();
	}

	public OnClickListener onRecord() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				String output = null;
				try {
					output = File.createTempFile("audio", ".3gp", context.getCacheDir()).getPath();
				} catch (IOException e) {
					Log.e(LOG_TAG, "IOException");
				}
				((View) v.getParent()).setTag(output);
				audioController.record(output);
				v.setEnabled(false);
				((ViewGroup) v.getParent()).findViewById(R.id.audioStop)
						.setEnabled(true);
			}
		};
	}

	public OnClickListener onStop() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				audioController.stop();
				v.setEnabled(false);
				((ViewGroup) v.getParent()).findViewById(
						R.id.audioRecord).setEnabled(true);
				((ViewGroup) v.getParent()).findViewById(
						R.id.audioPlay).setEnabled(true);
			}
		};
	}

	public OnClickListener onPlay() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				audioController.addPlayObserver(new PlayObserver(v) {
					@Override
					public void playReachedEnd() {
						this.playButton.setEnabled(true);
					}
				});
				v.setEnabled(false);
				String file = (String) ((View) v.getParent()).getTag();
				audioController.play(file);
			}
		};
	}
}
