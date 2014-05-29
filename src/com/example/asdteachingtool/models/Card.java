package com.example.asdteachingtool.models;

import java.io.File;
import java.lang.reflect.Field;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.util.Log;

@Table(name = "Cards")
public class Card extends SecureModel {

	private static final String LOG_TAG = "Card";

	// ------ Owners ---------- //
	@Column(name = "Question", index = true)
	public Question question;

	@Column(name = "Option", index = true)
	public Option option;

	@Column(name = "Pecs", index = true)
	public Pecs pecs;

	// ------- Attributes -------//
	@Column(name = "Text")
	public String text;

	@Column(name = "Picture")
	public byte[] picture;

	@Column(name = "SoundPath")
	public String soundPath;

	public void beforeDelete() {
		if (soundPath != null && soundPath.length() > 0) {
			if (!new File(soundPath).delete()) {
				Log.e(LOG_TAG, "File not deleted: " + soundPath);
			}
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getSoundPath() {
		return soundPath;
	}

	public void setSoundPath(String soundPath) {
		this.soundPath = soundPath;
	}
}
