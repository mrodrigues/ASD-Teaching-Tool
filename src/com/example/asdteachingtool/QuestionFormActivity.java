package com.example.asdteachingtool;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.example.asdteachingtool.factories.AudioControllerFactories;
import com.example.asdteachingtool.methodobjects.ReceivePicture;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;
import com.example.asdteachingtool.utils.FileUtils;

public class QuestionFormActivity extends Activity {

	public static final String EXTRA_QUESTION_ID = "com.example.asdteachingtool.QUESTION_ID";

	private final int TAKE_PICTURE = 0;

	private EditText questionTitleTextView;
	private ImageView questionThumbnail;
	private ViewGroup optionsContainer;

	private ReceivePicture receivePicture;
	private AudioControllerFactories audioControllerFactories;

	private Question question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		audioControllerFactories = new AudioControllerFactories(this);

		setContentView(R.layout.activity_form_question);
		questionTitleTextView = (EditText) findViewById(R.id.questionTitle);
		questionThumbnail = (ImageView) findViewById(R.id.questionPicture);
		optionsContainer = (ViewGroup) findViewById(R.id.options_container);
		optionsContainer.removeAllViews();

		Long questionId = getIntent().getLongExtra(EXTRA_QUESTION_ID, -1);
		if (questionId == -1) {
			this.question = new Question();
			((ViewGroup) findViewById(R.id.questionForm))
					.removeView(findViewById(R.id.deleteQuestion));
		} else {
			ActiveAndroid.clearCache();
			this.question = Question.load(Question.class, questionId);
			setTitle(getString(R.string.edit_question) + " " + question.title);
		}
		updateView();

		// Show the Up button in the action bar.
		setupActionBar();
	}

	private void updateView() {
		questionTitleTextView.setText(question.title);
		new ReceivePicture(questionThumbnail).receive(question.picture);

		for (Option option : question.options()) {
			newOptionView(option);
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
		getMenuInflater().inflate(R.menu.new_question, menu);
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

	public void questionTakePicture(View v) {
		receivePicture = new ReceivePicture(questionThumbnail);
		takePicture();
	}

	public void takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				receivePicture.receive((Bitmap) extras.get("data"));
				receivePicture = null;
			}
			break;
		}
	}

	public void saveQuestion(View v) {
		updateQuestion();
		back();
	}

	public void updateQuestion() {
		ActiveAndroid.beginTransaction();
		try {
			question.title = questionTitleTextView.getText().toString();
			question.picture = (byte[]) questionThumbnail.getTag();

			deleteOptions();

			for (int i = 0; i < optionsContainer.getChildCount(); i++) {
				ViewGroup optionView = (ViewGroup) optionsContainer
						.getChildAt(i);
				Option option = new Option();

				option.question = question;
				option.text = ((EditText) optionView
						.findViewById(R.id.optionText)).getText().toString();
				option.correct = ((CheckBox) optionView
						.findViewById(R.id.optionCorrect)).isChecked();
				option.picture = (byte[]) optionView.findViewById(
						R.id.optionPicture).getTag();
				String path = (String) optionView.findViewById(
						R.id.audioController).getTag();
				if (path != null) {
					System.err.println(path);
					File tempSoundFile = new File(path);// (String)
														// optionView.findViewById(R.id.audioController).getTag());
					File soundFile = new File(
							Environment.getExternalStorageDirectory(), "audio/"
									+ tempSoundFile.getName());
					FileUtils.copyFile(tempSoundFile, soundFile);
					option.soundPath = soundFile.getPath();
				}
				option.save();
			}
			question.save();

			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}

	private void deleteOptions() {
		for (Option option : question.options()) {
			option.delete();
		}
	}

	private void back() {
		NavUtils.navigateUpFromSameTask(this);
	}

	public void deleteQuestion(View v) {
		confirm(R.string.confirm_delete_question,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActiveAndroid.beginTransaction();
						try {
							deleteOptions();
							question.delete();
							ActiveAndroid.setTransactionSuccessful();
						} finally {
							ActiveAndroid.endTransaction();
							back();
						}
					}
				});
	}

	public void newOptionView(View v) {
		newOptionView(new Option());
	}

	private void newOptionView(Option option) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.option, null);
		populateOptionView(option, v);
		((ViewGroup) findViewById(R.id.options_container)).addView(v);
	}

	private void populateOptionView(Option option, ViewGroup v) {
		((EditText) v.findViewById(R.id.optionText)).setText(option.text);
		((CheckBox) v.findViewById(R.id.optionCorrect)).setChecked(option
				.isCorrect());
		new ReceivePicture((ImageView) v.findViewById(R.id.optionPicture))
				.receive(option.picture);
		v.findViewById(R.id.optionTakePicture).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						receivePicture = new ReceivePicture(
								(ImageView) ((ViewGroup) v.getParent())
										.findViewById(R.id.optionPicture));
						takePicture();
					}
				});

		v.findViewById(R.id.audioController).setTag(option.soundPath);

		View record = v.findViewById(R.id.audioRecord);
		record.setOnClickListener(audioControllerFactories.onRecord());

		View stop = v.findViewById(R.id.audioStop);
		stop.setEnabled(false);
		stop.setOnClickListener(audioControllerFactories.onStop());

		View play = v.findViewById(R.id.audioPlay);
		if (option.soundPath == null || option.soundPath.length() == 0) {
			play.setEnabled(false);
		}
		play.setOnClickListener(audioControllerFactories.onPlay());
	}

	private void confirm(int message,
			android.content.DialogInterface.OnClickListener action) {
		new AlertDialog.Builder(this).setMessage(getString(message))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, action)
				.setNegativeButton(android.R.string.no, null).show();
	}
}
