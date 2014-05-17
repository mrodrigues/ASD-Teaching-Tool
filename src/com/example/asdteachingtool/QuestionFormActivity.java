package com.example.asdteachingtool;

import java.io.ByteArrayOutputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;

public class QuestionFormActivity extends Activity {

	public static final String EXTRA_QUESTION_ID = "com.example.asdteachingtool.QUESTION_ID";

	private final int TAKE_PICTURE = 0;

	private EditText questionTitleTextView;
	private ImageView questionThumbnail;
	private ViewGroup optionsContainer;
	private ImageView pictureView;

	private Question question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form_question);
		questionTitleTextView = (EditText) findViewById(R.id.questionTitle);
		questionThumbnail = (ImageView) findViewById(R.id.questionPicture);
		optionsContainer = (ViewGroup) findViewById(R.id.options_container);
		optionsContainer.removeAllViews();

		Long id = getIntent().getLongExtra(EXTRA_QUESTION_ID, -1);
		if (id == -1) {
			this.question = new Question();
		} else {
			ActiveAndroid.clearCache();
			this.question = Question.load(Question.class, id);
			setTitle(getString(R.string.edit_question) + " " + question.title);
		}
		updateView();

		// Show the Up button in the action bar.
		setupActionBar();
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		System.err.println("===================================");
		System.err.println("onConfigurationChanged");
		System.err.println("===================================");
		super.onConfigurationChanged(newConfig);
		updateView();
	}

	public void questionTakePicture(View v) {
		pictureView = questionThumbnail;
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
				pictureView.setTag(extras.get("data"));
				pictureView = null;
				updateQuestion();
			}
			break;
		}
	}

	private byte[] getPictureFromView(ImageView view) {
		Bitmap picture = (Bitmap) view.getTag();
		if (picture != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
			return stream.toByteArray();
		}
		return null;
	}

	public void updateQuestion() {
		question.title = questionTitleTextView.getText().toString();
		question.picture = getPictureFromView(questionThumbnail);

		for (int i = 0; i < optionsContainer.getChildCount(); i++) {
			ViewGroup optionView = (ViewGroup) optionsContainer.getChildAt(i);

			Option option = (Option) optionView.getTag();
			option.text = ((EditText) optionView.findViewById(R.id.optionText))
					.getText().toString();
			option.text = "WTFFFFFFFFF";

			System.err.println("==================");
			System.err.println("updateQuestion");
			Option o = question.options().get(
					question.options().indexOf(option));
			System.err.println(option.hashCode());
			System.err.println(o.hashCode());
			System.err.println(option);
			System.err.println(o);
			System.err.println(option == o);
			System.err.println(option.equals(o));
			o.text = "WTFFFFFFFFF";
			System.err.println(o.text);
			System.err.println(((EditText) optionView
					.findViewById(R.id.optionText)).getText().toString());
			System.err.println(option.text);
			System.err.println("==================");

			option.picture = getPictureFromView((ImageView) optionView
					.findViewById(R.id.optionPicture));
			if (question.options().indexOf(option) == -1) {
				question.options().add(option);
			}
		}

		updateView();
	}

	private void updateView() {

		System.err.println("==================");
		System.err.println("updateView");
		System.err.println("==================");
		questionTitleTextView.setText(question.title);

		if (question.picture != null && question.picture.length > 0) {
			questionThumbnail.setVisibility(View.VISIBLE);
			questionThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(
					question.picture, 0, question.picture.length));
		} else {
			questionThumbnail.setVisibility(View.INVISIBLE);
		}

		for (Option option : question.options()) {
			ViewGroup v = (ViewGroup) optionsContainer.findViewWithTag(option);
			if (v == null) {
				System.err.println("==================");
				System.err.println("newOption");
				System.err.println("==================");
				newOption(option);
			} else {
				System.err.println("==================");
				System.err.println("populateOptionView");
				System.err.println("==================");
				populateOptionView(option, v);
			}
			System.err.println("==================");
			System.err.println("updateView inside options");
			System.err.println(option.hashCode());
			System.err.println(option.text);
			System.err.println("==================");
		}
	}

	public void saveQuestion(View v) {
		updateQuestion();
		question.save();
		NavUtils.navigateUpFromSameTask(this);
	}

	public void newOption() {
		newOption(new Option());
	}

	public void newOption(Option option) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.option, null);
		populateOptionView(option, v);
		((ViewGroup) findViewById(R.id.options_container)).addView(v);
	}

	public void populateOptionView(Option option, ViewGroup v) {
		v.setTag(option);
		((EditText) v.findViewById(R.id.optionText)).setText(option.text);
		Button takePictureButton = (Button) v
				.findViewById(R.id.optionTakePicture);
		takePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pictureView = (ImageView) ((ViewGroup) v.getParent())
						.findViewById(R.id.optionPicture);
				takePicture();
			}
		});
	}
}
