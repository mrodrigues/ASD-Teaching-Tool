package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

@Table(name = "Questions")
public class Question extends SecureModel implements Sortable {

	@Column(name = "Published")
	public Boolean published;

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
			setPosition(PositionSequence.nextPosition());
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
		return scoped().execute();
	}

	public static Question last() {
		List<Question> question = all();
		if (question.isEmpty()) {
			return null;
		} else {
			return question.get(question.size() - 1);
		}
	}
	
	public static List<Question> published() {
		return scoped().where("Published = ?", true).execute();
	}

	private static From scoped() {
		return new Select().from(Question.class).orderBy("Position ASC");
	}

	public static long[] publishedIds() {
		List<Question> questions = published();
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String getName() {
		return getText();
	}

	public Boolean isPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

}
