package com.example.asdteachingtool.listeners;

import android.view.View;

public abstract class PlayerListener {
	protected View playButton;

	public PlayerListener(View playButton) {
		super();
		this.playButton = playButton;
	}

	/**
	 * Action to be performed after the sound has ended.
	 * 
	 * @return True if this observer should be removed from the list of
	 *         observers. False otherwise.
	 */
	public abstract boolean playReachedEnd();
}
