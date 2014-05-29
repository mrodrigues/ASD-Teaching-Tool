package com.example.asdteachingtool;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.example.asdteachingtool.components.TempFilesManager;
import com.example.asdteachingtool.factories.AudioControllerFactories;
import com.example.asdteachingtool.methodobjects.ReceivePicture;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;
import com.example.asdteachingtool.utils.FileUtils;

import eu.janmuller.android.simplecropimage.CropImage;

public class QuestionFormActivity extends Activity {

	private static final String LOG_TAG = "QuestionFormActivity";

	public static final String EXTRA_QUESTION_ID = "com.example.asdteachingtool.QUESTION_ID";

	private static final int REQUEST_CODE_TAKE_PICTURE = 0;
	private static final int REQUEST_CODE_CROP_IMAGE = 1;

	private EditText questionTitleTextView;
	private ImageView questionThumbnail;
	private ViewGroup optionsContainer;

	private ReceivePicture receivePicture;
	private AudioControllerFactories audioControllerFactories;

	private Question question;
	private String targetFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		audioControllerFactories = new AudioControllerFactories(this);

		setContentView(R.layout.activity_question_form);
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
		targetFile = TempFilesManager.getInstance().createTempFile("picture",
				".jpg", getExternalCacheDir());
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri path = Uri.fromFile(new File(targetFile));
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);

		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);

		String pickTitle = getString(R.string.pick_intent_picture);
		Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				new Intent[] { takePictureIntent });

		startActivityForResult(chooserIntent, REQUEST_CODE_TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				Boolean isCamera = null;
				if (data == null) {
					isCamera = true;
				} else {
					final String action = data.getAction();
					if (action == null) {
						isCamera = false;
					} else {
						isCamera = action
								.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					}
				}
				if (!isCamera) {
					if (data == null) {
						targetFile = null;
					} else {
						File originalFile = new File(FileUtils.getPathFor(this,
								data.getData()));
						FileUtils.copyFile(originalFile, new File(targetFile));
					}

				}
				Intent intent = new Intent(this, CropImage.class);
				intent.putExtra(CropImage.IMAGE_PATH, targetFile);
				targetFile = null;
				intent.putExtra(CropImage.SCALE, true);
				intent.putExtra(CropImage.ASPECT_X, 1);
				intent.putExtra(CropImage.ASPECT_Y, 1);
				startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
			}
			break;
		case REQUEST_CODE_CROP_IMAGE:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(CropImage.IMAGE_PATH);
				if (path == null) {
					Log.e(LOG_TAG, "Crop image is null");
					return;
				}

				receivePicture.receive(path);
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
			question.save();

			deleteOptions();

			for (int i = 0; i < optionsContainer.getChildCount(); i++) {
				ViewGroup optionView = (ViewGroup) optionsContainer
						.getChildAt(i);
				Option option = new Option();

				option.question = question;
				option.correct = ((CheckBox) optionView
						.findViewById(R.id.optionCorrect)).isChecked();
				EditText optionText = (EditText) optionView
						.findViewById(R.id.optionText);
				if (optionText.getVisibility() == View.VISIBLE) {
					option.text = optionText.getText().toString();
				}
				if (optionView.findViewById(R.id.optionPictureContainer)
						.getVisibility() == View.VISIBLE) {
					option.picture = (byte[]) optionView.findViewById(
							R.id.optionPicture).getTag();
				}
				String path = (String) optionView.findViewById(
						R.id.audioController).getTag();
				if (path != null) {
					File tempSoundFile = new File(path);
					File soundFile = new File(
							Environment.getExternalStorageDirectory(), "audio/"
									+ tempSoundFile.getName());
					FileUtils.copyFile(tempSoundFile, soundFile);
					option.soundPath = soundFile.getPath();
				}
				option.save();
			}

			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}

	private void deleteOptions() {
		for (Option option : question.options()) {
			option.secureDelete();
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

	public void deleteOption(final View button) {
		confirm(R.string.confirm_delete_option,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						View option = (View) button.getParent();
						ViewGroup optionsContainer = (ViewGroup) option
								.getParent();
						optionsContainer.removeView(option);
					}
				});
	}

	public void selectOptionType(View button) {
		selectOptionType((RadioGroup) button.getParent());
	}

	public void selectOptionType(RadioGroup radioGroup) {
		ViewGroup optionContainer = (ViewGroup) radioGroup.getParent();
		View optionText = optionContainer.findViewById(R.id.optionText);
		View optionPicture = optionContainer
				.findViewById(R.id.optionPictureContainer);
		RadioButton typePicture = (RadioButton) radioGroup
				.findViewById(R.id.optionTypePicture);
		if (typePicture.isChecked()) {
			optionText.setVisibility(View.GONE);
			optionPicture.setVisibility(View.VISIBLE);
		} else {
			optionText.setVisibility(View.VISIBLE);
			optionPicture.setVisibility(View.GONE);
		}
	}

	public void newOptionView(View v) {
		newOptionView(new Option());
	}

	private void newOptionView(Option option) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.option_form, null);
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

		int optionType = option.hasText() ? R.id.optionTypeText
				: R.id.optionTypePicture;
		((RadioButton) v.findViewById(optionType)).setChecked(true);

		selectOptionType((RadioGroup) v.findViewById(R.id.optionTypeContainer));

		if (option.hasSound()) {
			String sound = TempFilesManager.getInstance().createTempFile(
					"audio", ".3pg", getExternalCacheDir());
			FileUtils.copyFile(new File(option.soundPath), new File(sound));
			v.findViewById(R.id.audioController).setTag(sound);
		}

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

	@Override
	protected void onStop() {
		TempFilesManager.getInstance().clean();
		super.onStop();
	}
}
