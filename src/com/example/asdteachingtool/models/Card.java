package com.example.asdteachingtool.models;

import java.io.File;
import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

@Table(name = "Cards")
public class Card extends SecureModel implements Sortable {

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

	@Column(name = "Position", notNull = true)
	public Integer position;

	@Column(name = "Category")
	public Integer category;

	@Override
	public void beforeSave() {
		if (!isPersisted()) {
			setPosition(PositionSequence.nextPosition());
		}
	}

	public void beforeDelete() {
		if (soundPath != null && soundPath.length() > 0) {
			if (!new File(soundPath).delete()) {
				Log.e(LOG_TAG, "File not deleted: " + soundPath);
			}
		}
	}

	public static List<Card> byCategory(Integer categoryId) {
		return new Select().from(Card.class)
				.where("Category = ?", categoryId)
				.orderBy("Position ASC").execute();
	}
	
	public Boolean hasText() {
		return text != null && text.length() > 0;
	}

	public Boolean hasSound() {
		return soundPath != null && soundPath.length() > 0;
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

	@Override
	public Integer getPosition() {
		return position;
	}

	@Override
	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String getName() {
		return getText();
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}
}
