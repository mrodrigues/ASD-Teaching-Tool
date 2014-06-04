package com.example.asdteachingtool;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.example.asdteachingtool.components.Sorter;
import com.example.asdteachingtool.components.TempFilesManager;
import com.example.asdteachingtool.factories.AudioControllerFactory;
import com.example.asdteachingtool.methodobjects.ReceivePicture;
import com.example.asdteachingtool.models.Card;
import com.example.asdteachingtool.utils.FileUtils;

import eu.janmuller.android.simplecropimage.CropImage;

public class CardFormActivity extends Activity implements OnClickListener {

	private static final String LOG_TAG = "CardFormActivity";

	public static final String EXTRAS_CATEGORY_ID = "com.example.asdteachingtool.CATEGORY_ID";

	private static final int REQUEST_CODE_TAKE_PICTURE = 0;
	private static final int REQUEST_CODE_CROP_IMAGE = 1;

	private ReceivePicture receivePicture;
	private AudioControllerFactory audioControllerFactory;

	private Card card;
	private String targetFile;

	private int categoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		audioControllerFactory = new AudioControllerFactory(this);
		categoryId = getIntent().getIntExtra(
				CardFormActivity.EXTRAS_CATEGORY_ID, -1);

		setContentView(R.layout.activity_card_form);

		Long cardId = getIntent().getLongExtra(Sorter.EXTRA_MODEL_ID, -1);
		if (cardId == -1) {
			this.card = new Card();
		} else {
			ActiveAndroid.clearCache();
			this.card = Card.load(Card.class, cardId);
			setTitle(getString(R.string.edit_model) + " " + card.getText());
		}
		updateView();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	private void updateView() {
		((EditText) findViewById(R.id.cardText)).setText(card.getText());
		new ReceivePicture((ImageView) findViewById(R.id.cardPicture))
				.receive(card.getPicture());
		findViewById(R.id.cardTakePicture).setOnClickListener(this);

		if (card.hasSound()) {
			String sound = TempFilesManager.getInstance()
					.createTempFile("audio", ".3pg", getExternalCacheDir())
					.getPath();
			FileUtils.copyFile(new File(card.getSoundPath()), new File(sound));
			findViewById(R.id.audioController).setTag(sound);
		}

		View record = findViewById(R.id.audioRecord);
		record.setOnClickListener(audioControllerFactory.onRecord());

		View stop = findViewById(R.id.audioStop);
		stop.setEnabled(false);
		stop.setOnClickListener(audioControllerFactory.onStop());

		View play = findViewById(R.id.audioPlay);
		if (!card.hasSound()) {
			play.setEnabled(false);
		}
		play.setOnClickListener(audioControllerFactory.onPlay());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cardTakePicture:
			receivePicture = new ReceivePicture(
					(ImageView) findViewById(R.id.cardPicture));
			takePicture();
			break;
		}
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
				System.err.println("========== after crop ===========");
				System.err.println(findViewById(R.id.audioController).getTag());
				System.err.println("=================================");
				receivePicture.receive(path);
				receivePicture = null;
			}
			break;
		}
	}

	public void saveCard(View v) {
		card.setCategory(categoryId);
		card.setText(((EditText) findViewById(R.id.cardText)).getText()
				.toString());
		card.setPicture((byte[]) findViewById(R.id.cardPicture).getTag());
		String path = (String) findViewById(R.id.audioController).getTag();
		if (path != null) {
			File tempSoundFile = new File(path);
			File soundFile = TempFilesManager.createUnmanagedTempFile("audio",
					".3gp", Environment.getExternalStorageDirectory());
			FileUtils.copyFile(tempSoundFile, soundFile);
			card.setSoundPath(soundFile.getPath());
		}
		card.secureSave();
		back();
	}

	public void deleteCard(View v) {
		confirm(R.string.confirm_delete_question,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						card.secureDelete();
						back();
					}
				});
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

	private void back() {
		finish();
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
		getMenuInflater().inflate(R.menu.card_form, menu);
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

}
