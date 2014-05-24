package com.example.asdteachingtool.models;

import java.io.File;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.util.Log;

@Table(name = "Options")
public class Option extends Model {
	
	private static final String LOG_TAG = "Option";
	
	@Column(name = "Correct")
	public Boolean correct;
	
	@Column(name = "Text")
	public String text;
	
	@Column(name = "Picture")
	public byte[] picture;
	
	@Column(name = "SoundPath")
	public String soundPath;
	
	@Column(name = "Question")
	public Question question;

	public Boolean isCorrect() {
		return correct == null ? false : correct;
	}
	
	public Boolean hasText() {
		return text != null && text.length() > 0;
	}
	
	public Boolean hasSound() {
		return soundPath != null && soundPath.length() > 0;
	}
	
	public void secureDelete() {
		if (soundPath != null && soundPath.length() > 0) {
			if (! new File(soundPath).delete()) {
				Log.e(LOG_TAG, "File not deleted: " + soundPath);
			}
		}
		delete();
	}
}
