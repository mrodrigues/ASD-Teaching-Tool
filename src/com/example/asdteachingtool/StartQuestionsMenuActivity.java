package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.asdteachingtool.models.Question;

public class StartQuestionsMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_questions_menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		View startQuestions = findViewById(R.id.startQuestions);
		if (Question.published().isEmpty()) {
			startQuestions.setEnabled(false);
		} else {
			startQuestions.setEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_menu, menu);
		return true;
	}
	
}
