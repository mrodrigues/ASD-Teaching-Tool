package com.example.asdteachingtool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activeandroid.ActiveAndroid;
import com.example.asdteachingtool.models.Question;

public class QuestionsListActivity extends Activity {

	private RadioGroup questionsList;
	private Integer lastSelectedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questions_list);

		questionsList = (RadioGroup) findViewById(R.id.questions_list);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	protected void onStart() {
		super.onStart();
		lastSelectedIndex = null;
		updateView();
	}

	private void updateView() {
		questionsList.removeAllViews();
		final View up = findViewById(R.id.up_question);
		final View down = findViewById(R.id.down_question);
		final View edit = findViewById(R.id.edit_question);
		for (Question question : Question.all()) {
			RadioButton questionView = new RadioButton(this);
			questionView.setText(question.getTitle());
			questionView.setTag(question);
			questionView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					up.setEnabled(true);
					down.setEnabled(true);
					edit.setEnabled(true);
				}
			});
			questionsList.addView(questionView);
		}
		if (lastSelectedIndex != null) {
			((RadioButton) questionsList.getChildAt(lastSelectedIndex))
					.setChecked(true);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void newQuestion(View view) {
		startActivity(new Intent(this, QuestionFormActivity.class));
	}

	public void editQuestion(View v) {
		Intent intent = new Intent(v.getContext(), QuestionFormActivity.class);
		View selected = selectedQuestion();
		if (selected != null) {
			intent.putExtra(QuestionFormActivity.EXTRA_QUESTION_ID,
					((Question) selected.getTag()).getId());
			startActivity(intent);
		}
	}

	public void upQuestion(View v) {
		int index = selectedIndex();
		if (index > 0) {
			changePositions(index, index - 1);
		}
	}

	public void downQuestion(View v) {
		int index = selectedIndex();
		if (index < questionsList.getChildCount() - 1 && index > -1) {
			changePositions(index, index + 1);
		}
	}

	private void changePositions(int selectedIndex, int targetIndex) {
		RadioButton selectedButton = (RadioButton) questionsList
				.getChildAt(selectedIndex);
		RadioButton targetButton = (RadioButton) questionsList
				.getChildAt(targetIndex);
		Question selected = (Question) selectedButton.getTag();
		Question target = (Question) targetButton.getTag();
		ActiveAndroid.beginTransaction();
		try {
			selected.changePositions(target);
			selected.save();
			target.save();
			ActiveAndroid.setTransactionSuccessful();
			lastSelectedIndex = targetIndex;
		} finally {
			ActiveAndroid.endTransaction();
		}
		updateView();
	}

	private RadioButton selectedQuestion() {
		int id = questionsList.getCheckedRadioButtonId();
		if (id < 0) {
			return null;
		} else {
			return (RadioButton) questionsList.findViewById(id);
		}
	}

	private int selectedIndex() {
		RadioButton selected = selectedQuestion();
		if (selected != null) {
			return questionsList.indexOfChild(selected);
		}
		return -1;
	}
}
