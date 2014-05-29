package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Questions")
public class Question extends SecureModel {

	@Column(name = "Title")
	private String title;

	private Card card;

	public void beforeDelete() {
		card().secureDelete();
		for (Option option : options()) {
			option.secureDelete();
		}
	}

	public void afterSave() {
		card().question = this;
		card().secureSave();
	}

	public Card card() {
		if (card == null) {
			card = first(Card.class);
		}
		return card;
	}

	public List<Option> options() {
		return secureGetMany(Option.class);
	}

	public static List<Question> all() {
		return new Select().from(Question.class).execute();
	}

	public static long[] allIds() {
		List<Question> questions = all();
		long[] ids = new long[questions.size()];
		for (int i = 0; i < questions.size(); i++) {
			ids[i] = questions.get(i).getId();
		}
		return ids;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
