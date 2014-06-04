package com.example.asdteachingtool;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.asdteachingtool.components.Sorter;
import com.example.asdteachingtool.components.TempFilesManager;
import com.example.asdteachingtool.factories.AudioControllerFactory;
import com.example.asdteachingtool.methodobjects.ReceivePicture;
import com.example.asdteachingtool.models.Card;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;
import com.example.asdteachingtool.utils.FileUtils;

import eu.janmuller.android.simplecropimage.CropImage;

public class QuestionFormActivity extends Activity {

	private static final String LOG_TAG = "QuestionFormActivity";

	private static final int REQUEST_CODE_TAKE_PICTURE = 0;
	private static final int REQUEST_CODE_CROP_IMAGE = 1;

	private ViewGroup optionsContainer;

	private ReceivePicture receivePicture;
	private AudioControllerFactory audioControllerFactory;

	private Question question;
	private String targetFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		audioControllerFactory = new AudioControllerFactory(this);

		setContentView(R.layout.activity_question_form);
		optionsContainer = (ViewGroup) findViewById(R.id.options_container);
		optionsContainer.removeAllViews();

		Long questionId = getIntent().getLongExtra(Sorter.EXTRA_MODEL_ID, -1);
		if (questionId == -1) {
			this.question = new Question();
			findViewById(R.id.deleteQuestion).setVisibility(View.GONE);
		} else {
			ActiveAndroid.clearCache();
			this.question = Question.load(Question.class, questionId);
			setTitle(getString(R.string.edit_model) + " " + question.getText());
		}
		updateView();

		// Show the Up button in the action bar.
		setupActionBar();
	}

	private void updateView() {
		populateCardView(question.card(), findViewById(R.id.questionForm));
		((CheckBox) findViewById(R.id.questionPublished)).setChecked(question
				.isPublished());

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

	public void takePicture() {
		targetFile = TempFilesManager.getInstance()
				.createTempFile("picture", ".jpg", getExternalCacheDir())
				.getPath();
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
			updateCard(question.card(), findViewById(R.id.questionForm));
			question.setPublished(((CheckBox) findViewById(R.id.questionPublished))
					.isChecked());
			question.secureSave();

			deleteOptions();
			for (int i = 0; i < optionsContainer.getChildCount(); i++) {
				ViewGroup optionView = (ViewGroup) optionsContainer
						.getChildAt(i);

				Option option = new Option();

				option.question = question;
				option.correct = ((CheckBox) optionView
						.findViewById(R.id.optionCorrect)).isChecked();
				updateCard(option.card(), optionView);
				option.secureSave();
			}

			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}

	private void updateCard(Card card, View v) {
		EditText cardText = (EditText) v.findViewById(R.id.cardText);
		if (cardText.getVisibility() == View.VISIBLE) {
			card.setText(cardText.getText().toString());
		}
		if (v.findViewById(R.id.cardPictureContainer).getVisibility() == View.VISIBLE) {
			card.setPicture((byte[]) v.findViewById(R.id.cardPicture).getTag());
		}
		String path = (String) v.findViewById(R.id.audioController).getTag();
		if (path != null) {
			File tempSoundFile = new File(path);
			File soundFile = TempFilesManager.createUnmanagedTempFile("audio",
					".3gp", Environment.getExternalStorageDirectory());
			FileUtils.copyFile(tempSoundFile, soundFile);
			card.setSoundPath(soundFile.getPath());
		}
	}

	private void deleteOptions() {
		for (Option option : question.options()) {
			if (option.isPersisted()) {
				option.secureDelete();
			}
		}
	}

	private void back() {
		finish();
	}

	public void deleteQuestion(View v) {
		confirm(R.string.confirm_delete_question,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						question.secureDelete();
						back();
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
		View optionText = optionContainer.findViewById(R.id.cardText);
		View optionPicture = optionContainer
				.findViewById(R.id.cardPictureContainer);
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
		((CheckBox) v.findViewById(R.id.optionCorrect)).setChecked(option
				.isCorrect());

		int optionType = option.hasText() ? R.id.optionTypeText
				: R.id.optionTypePicture;
		((RadioButton) v.findViewById(optionType)).setChecked(true);
		selectOptionType((RadioGroup) v.findViewById(R.id.optionTypeContainer));
		populateCardView(option.card(), v);
	}

	private void populateCardView(Card card, View v) {
		((EditText) v.findViewById(R.id.cardText)).setText(card.getText());
		new ReceivePicture((ImageView) v.findViewById(R.id.cardPicture))
				.receive(card.getPicture());
		v.findViewById(R.id.cardTakePicture).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						receivePicture = new ReceivePicture(
								(ImageView) ((ViewGroup) v.getParent())
										.findViewById(R.id.cardPicture));
						takePicture();
					}
				});

		if (card.hasSound()) {
			String sound = TempFilesManager.getInstance()
					.createTempFile("audio", ".3pg", getExternalCacheDir())
					.getPath();
			FileUtils.copyFile(new File(card.getSoundPath()), new File(sound));
			v.findViewById(R.id.audioController).setTag(sound);
		}

		View record = v.findViewById(R.id.audioRecord);
		record.setOnClickListener(audioControllerFactory.onRecord());

		View stop = v.findViewById(R.id.audioStop);
		stop.setEnabled(false);
		stop.setOnClickListener(audioControllerFactory.onStop());

		View play = v.findViewById(R.id.audioPlay);
		if (!card.hasSound()) {
			play.setEnabled(false);
		}
		play.setOnClickListener(audioControllerFactory.onPlay());
	}

	private void confirm(int message,
			android.content.DialogInterface.OnClickListener action) {
		new AlertDialog.Builder(this).setMessage(getString(message))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, action)
				.setNegativeButton(android.R.string.no, null).show();
	}

	@Override
	protected void onDestroy() {
		TempFilesManager.getInstance().clean();
		super.onDestroy();
	}
}
