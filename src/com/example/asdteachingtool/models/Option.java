package com.example.asdteachingtool.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Options")
public class Option extends Model {
	
	@Column(name = "Correct")
	public Boolean correct;
	
	@Column(name = "Text")
	public String text;
	
	@Column(name = "Question")
	public Question question;
}
