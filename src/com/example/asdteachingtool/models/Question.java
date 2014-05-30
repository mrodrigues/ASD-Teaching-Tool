package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

@Table(name = "Questions")
public class Question extends SecureModel {

	@Column(name = "Title")
	public String title;

	@Column(name = "Position", notNull = true)
	public Integer position;

	private Card card;

	public void beforeDelete() {
		card().secureDelete();
		for (Option option : options()) {
			option.secureDelete();
		}
	}

	public void beforeSave() {
		if (!isPersisted()) {
			setPosition(0);
			Question last = last();
			if (last != null) {
				setPosition(last.getPosition() + 1);
			}
		}
	}

	public void afterSave() {
		card().question = this;
		card().secureSave();
	}

	private Card card() {
		if (card == null) {
			card = first(Card.class);
		}
		return card;
	}

	public List<Option> options() {
		return secureGetMany(Option.class);
	}

	public static List<Question> all() {
		return scoped().execute();
	}

	public static Question last() {
		List<Question> question = scoped().execute();
		if (question.isEmpty()) {
			return null;
		} else {
			return question.get(question.size() - 1);
		}
	}

	private static From scoped() {
		return new Select().from(Question.class).orderBy("Position ASC");
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void changePositions(Question target) {
		Integer aux = getPosition();
		setPosition(target.getPosition());
		target.setPosition(aux);
	}

}
