package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Questionnaires")
public class Questionnaire extends Model {
	
	@Column(name = "Title")
	public String title;
	
	public List<Option> options() {
		return getMany(Option.class, "Questionnaire");
	}
}
