package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Questionnaires")
public class Questionnaire extends Model {
	
	@Column(name = "Title")
	public String title;
	
	public List<Option> options() {
		return getMany(Option.class, "Questionnaire");
	}
	
	public static List<Questionnaire> all() {
		return new Select().from(Questionnaire.class).execute();
	}
}
