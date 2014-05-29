package com.example.asdteachingtool.factories;

import android.support.v4.app.NavUtils;
import android.view.View;

import com.example.asdteachingtool.QuestionActivity;
import com.example.asdteachingtool.R;
import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.listeners.PlayerListener;

public class PlayObserverFactory {

	private AudioController audioController;
	private QuestionActivity activity;

	public PlayObserverFactory(QuestionActivity activity,
			AudioController audioController) {
		super();
		this.audioController = audioController;
		this.activity = activity;
	}

	public void afterCorrectQuestion(View v) {
		addObserver(new PlayerListener(v) {

			@Override
			public boolean playReachedEnd() {
				audioController.play(R.raw.correct);
				activity.nextQuestion();
				return true;
			}
		});
	}

	public void afterWrongQuestion(View v) {
		addObserver(new PlayerListener(v) {

			@Override
			public boolean playReachedEnd() {
				audioController.play(R.raw.wrong);
				return true;
			}
		});
	}

	public void afterCompletingAllQuestions(View v) {
		addObserver(new PlayerListener(v) {

			@Override
			public boolean playReachedEnd() {
				audioController.play(R.raw.correct);
				NavUtils.navigateUpFromSameTask(activity);
				return true;
			}
		});
	}

	private void addObserver(PlayerListener observer) {
		if (audioController.isPlaying()) {
			audioController.addPlayObserver(observer);
		} else {
			observer.playReachedEnd();
		}
	}

}
