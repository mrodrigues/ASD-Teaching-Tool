package com.example.asdteachingtool.models;

import com.activeandroid.annotation.Table;

@Table(name = "Positions")
public class PositionSequence extends SecureModel {

	private PositionSequence() {
	}

	public static Integer nextPosition() {
		PositionSequence position = new PositionSequence();
		if (position.save() > -1) {
			return position.getId().intValue();
		}
		return null;
	}

}
