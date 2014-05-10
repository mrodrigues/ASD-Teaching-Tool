package com.example.asdteachingtool.models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Questions")
public class Question extends Model {
	
	@Column(name = "Title")
	public String title;
	
	public List<Option> options() {
		return getMany(Option.class, "Question");
	}
	
	public static List<Question> all() {
		return new Select().from(Question.class).execute();
	}
}
