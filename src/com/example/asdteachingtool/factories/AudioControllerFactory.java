package com.example.asdteachingtool.factories;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.asdteachingtool.R;
import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.components.TempFilesManager;
import com.example.asdteachingtool.listeners.PlayerListener;

public class AudioControllerFactory {
	
	private AudioController audioController;
	private Context context;

	public AudioControllerFactory(Context context) {
		this.context = context;
		this.audioController = new AudioController(context);
	}

	public OnClickListener onRecord() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				String output = TempFilesManager.getInstance().createTempFile("audio", ".3gp", context.getExternalCacheDir());
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
				audioController.addPlayObserver(new PlayerListener(v) {
					@Override
					public boolean playReachedEnd() {
						this.playButton.setEnabled(true);
						return false;
					}
				});
				v.setEnabled(false);
				String file = (String) ((View) v.getParent()).getTag();
				audioController.play(file);
			}
		};
	}
}
