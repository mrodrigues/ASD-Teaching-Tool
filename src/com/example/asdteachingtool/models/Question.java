package com.example.asdteachingtool.models;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Questions")
public class Question extends Model {
	
	public Question() {
		title = "";
	}
	
	@Column(name = "Title")
	public String title;
	
	@Column(name= "Picture")
	public byte[] picture;
	
	public List<Option> options() {
		if (getId() == null) {
			return new ArrayList<Option>();
		}
		return getMany(Option.class, "Question");
	}
	
	public static List<Question> all() {
		return new Select().from(Question.class).execute();
	}
}
