package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Options")
public class Option extends SecureModel {

	private static final String LOG_TAG = "Option";

	@Column(name = "Correct")
	public Boolean correct;

	@Column(name = "Question", notNull = true, index = true)
	public Question question;

	private Card card;

	public void beforeDelete() {
		card().secureDelete();
	}

	public void afterSave() {
		card().option = this;
		card().secureSave();
	}

	private Card card() {
		if (card == null) {
			card = first(Card.class);
		}
		return card;
	}

	public Boolean isCorrect() {
		return correct == null ? false : correct;
	}

	public Boolean hasText() {
		return card().text != null && card().text.length() > 0;
	}

	public Boolean hasSound() {
		return card().soundPath != null && card().soundPath.length() > 0;
	}

	public String getText() {
		return card().getText();
	}

	public void setText(String text) {
		card().setText(text);
	}

	public byte[] getPicture() {
		return card().getPicture();
	}

	public void setPicture(byte[] picture) {
		card().setPicture(picture);
	}

	public String getSoundPath() {
		return card().getSoundPath();
	}

	public void setSoundPath(String soundPath) {
		card().setSoundPath(soundPath);
	}
}
