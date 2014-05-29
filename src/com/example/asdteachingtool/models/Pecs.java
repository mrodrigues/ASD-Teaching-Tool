package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.annotation.Table;

@Table(name = "Pecs")
public class Pecs extends SecureModel {

	public List<Card> cards() {
		return secureGetMany(Card.class);
	}

	@Override
	public void beforeDelete() {
		for (Card card : cards()) {
			card.secureDelete();
		}
	}

	@Override
	public void afterSave() {
		for (Card card : cards()) {
			card.secureSave();
		}
	}
}
