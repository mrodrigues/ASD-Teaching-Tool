package com.example.asdteachingtool.observers;

import android.view.View;

public abstract class PlayObserver {
	protected View playButton;
	
	public PlayObserver(View playButton) {
		super();
		this.playButton = playButton;
	}

	public abstract void playReachedEnd();
}
