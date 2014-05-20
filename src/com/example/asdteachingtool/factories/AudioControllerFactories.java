package com.example.asdteachingtool.factories;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.example.asdteachingtool.R;
import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.observers.PlayObserver;

public class AudioControllerFactories {
	private AudioController audioController;

	public AudioControllerFactories() {
		this.audioController = new AudioController();
	}

	public OnClickListener onRecord() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				audioController.record();
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
				((View) v.getParent()).setTag(audioController.getOutputFile());
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
