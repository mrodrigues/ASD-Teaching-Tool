package com.example.asdteachingtool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.components.ConnectingStroke;
import com.example.asdteachingtool.factories.BitmapFactory;
import com.example.asdteachingtool.factories.PlayObserverFactory;
import com.example.asdteachingtool.listeners.LaunchThermometer;
import com.example.asdteachingtool.listeners.SelectListener;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;

public class QuestionActivity extends Activity {

	public final static String EXTRA_QUESTION_ID_INDEX = "com.example.asdteachingtool.QUESTION_ID_INDEX";
	public final static String EXTRA_QUESTIONS_IDS = "com.example.asdteachingtool.QUESTIONS_IDS";

	private final static String LOG_TAG = "QuestionAcivity";

	private Question question;
	private long[] questionsIds;
	private int questionIdIndex;
	private AudioController audioController;
	private GestureDetectorCompat gestureDetector;
	private List<View> options;
	private PlayObserverFactory playObserverFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new ArrayList<View>();
		gestureDetector = new GestureDetectorCompat(this,
				new LaunchThermometer(this));
		audioController = new AudioController(this);
		playObserverFactory = new PlayObserverFactory(this, audioController);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_question);

		questionIdIndex = getIntent().getIntExtra(EXTRA_QUESTION_ID_INDEX, -1);
		questionsIds = getIntent().getLongArrayExtra(EXTRA_QUESTIONS_IDS);
		if (questionIdIndex < 0) {
			Log.e(LOG_TAG, "Missing question id.");
			Toast.makeText(this, getString(R.string.error_opening_question),
					Toast.LENGTH_SHORT).show();
			NavUtils.navigateUpFromSameTask(this);
		}

		question = Question.load(Question.class, questionsIds[questionIdIndex]);

		updateView();
		ConnectingStroke stroke = new ConnectingStroke(this, Color.RED);
		addContentView(stroke, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		((ImageView) findViewById(R.id.questionPicture))
				.setOnTouchListener(new SelectListener(stroke, Color.BLUE,
						options));
	}

	private void updateView() {
		ImageView questionPicture = (ImageView) findViewById(R.id.questionPicture);
		questionPicture.setImageBitmap(BitmapFactory
				.decodeByteArray(question.getPicture()));

		for (Option option : question.options()) {
			newOptionView(option);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		audioController.play(question.getSoundPath());
	}

	private void newOptionView(Option option) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.option, null);
		populateOptionView(option, v);
		((ViewGroup) findViewById(R.id.options_container)).addView(v);
	}

	private void populateOptionView(Option option, ViewGroup v) {
		v.setTag(option);

		Button optionTextButton = (Button) v
				.findViewById(R.id.optionTextButton);
		ImageButton optionPictureButton = (ImageButton) v
				.findViewById(R.id.optionPictureButton);

		View optionButton = null;
		if (option.hasText()) {
			optionTextButton.setText(option.getText());
			optionPictureButton.setVisibility(View.GONE);
			optionButton = optionTextButton;
		} else {
			optionPictureButton.setImageBitmap(BitmapFactory
					.decodeByteArray(option.getPicture()));
			optionTextButton.setVisibility(View.GONE);
			optionButton = optionPictureButton;
		}
		options.add(optionButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questionnaire, menu);
		return true;
	}

	public void selectOption(View v) {
		Option option = optionFromView(v);
		audioController.play(option.getSoundPath());
		String message = null;
		if (option.isCorrect()) {
			if (lastQuestion()) {
				message = getString(R.string.completed_questions);
				playObserverFactory.afterCompletingAllQuestions(v);
			} else {
				message = getString(R.string.correct_answer);
				playObserverFactory.afterCorrectQuestion(v);
			}
		} else {
			playObserverFactory.afterWrongQuestion(v);
			message = getString(R.string.wrong_answer);
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private boolean lastQuestion() {
		return questionIdIndex + 1 >= questionsIds.length;
	}

	public void nextQuestion() {
		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(EXTRA_QUESTIONS_IDS, questionsIds);
		intent.putExtra(EXTRA_QUESTION_ID_INDEX, questionIdIndex + 1);
		startActivity(intent);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}

	private Option optionFromView(View v) {
		return (Option) ((View) v.getParent()).getTag();
	}

}
