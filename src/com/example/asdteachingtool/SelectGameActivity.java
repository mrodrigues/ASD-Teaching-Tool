package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.asdteachingtool.models.Question;

public class SelectGameActivity extends Activity {

	private static Context initialContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_game);
		initialContext = this;
	}

	@Override
	protected void onResume() {
		super.onResume();

		Integer visibility = null;
		if (Question.published().isEmpty()) {
			visibility = View.GONE;
		} else {
			visibility = View.VISIBLE;
		}
		findViewById(R.id.startQuestions).setVisibility(visibility);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_game, menu);
		return true;
	}

	public void startQuestionsMenu(View v) {
		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(QuestionActivity.EXTRA_QUESTIONS_IDS,
				Question.publishedIds());
		intent.putExtra(QuestionActivity.EXTRA_QUESTION_ID_INDEX, 0);
		startActivity(intent);
	}

	public void startPecsMenu(View v) {
		startActivity(new Intent(this, PecsActivity.class));
	}

	public void startConfigMenu(View v) {
		startActivity(new Intent(this, ConfigActivity.class));
	}

	public static Context getContext() {
		return initialContext;
	}
}
