package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.factories.BitmapFactory;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;

public class QuestionActivity extends Activity {

	public final static String EXTRA_QUESTION_ID = "com.example.asdteachingtool.QUESTION_ID";

	private Question question;
	private AudioController audioController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		audioController = new AudioController();
		setContentView(R.layout.activity_question);
		// ---- TEST SAMPLE ------
		if (Question.all().size() == 0) {
			question = new Question();
			question.title = "Feliz";
			question.save();

			Option option1 = new Option();
			option1.question = question;
			option1.text = "Rosto sorridente";
			option1.correct = true;
			option1.save();

			Option option2 = new Option();
			option2.question = question;
			option2.text = "Rosto triste";
			option2.correct = false;
			option2.save();
		} else {
			question = Question.all().get(0);
		}
		// -----------------------

		updateView();
	}

	private void updateView() {
		setTitle(question.title);
		ImageView questionPicture = (ImageView) findViewById(R.id.questionPicture);
		questionPicture.setImageBitmap(BitmapFactory
				.decodeByteArray(question.picture));

		for (Option option : question.options()) {
			newOptionView(option);
		}
	}

	private void newOptionView(Option option) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.option, null);
		populateOptionView(option, v);
		((ViewGroup) findViewById(R.id.options_container)).addView(v);
	}

	private void populateOptionView(Option option, ViewGroup v) {
		v.setTag(option);

		if (!option.hasSound()) {
			v.findViewById(R.id.optionPlaySound).setVisibility(View.GONE);
		}

		Button optionTextButton = (Button) v
				.findViewById(R.id.optionTextButton);
		ImageButton optionPictureButton = (ImageButton) v
				.findViewById(R.id.optionPictureButton);

		if (option.hasText()) {
			optionTextButton.setText(option.text);
			optionPictureButton.setVisibility(View.GONE);
		} else {
			optionPictureButton.setImageBitmap(BitmapFactory
					.decodeByteArray(option.picture));
			optionTextButton.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questionnaire, menu);
		return true;
	}

	public void selectOption(View v) {
		Option option = optionFromView(v);
		String message = null;
		if (option.isCorrect()) {
			message = "ACERTOU!!!";
			Intent intent = new Intent(v.getContext(),
					QuestionsListActivity.class);
			v.getContext().startActivity(intent);
		} else {
			message = "Errou...";
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void playSound(View v) {
		Option option = optionFromView(v);
		audioController.play(option.soundPath);
	}

	private Option optionFromView(View v) {
		return (Option) ((View) v.getParent()).getTag();
	}

}
