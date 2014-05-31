package com.example.asdteachingtool;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.asdteachingtool.components.AudioController;
import com.example.asdteachingtool.factories.BitmapFactory;
import com.example.asdteachingtool.models.Card;
import com.example.asdteachingtool.models.Category;

public class PecsActivity extends Activity {

	private AudioController audioController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.audioController = new AudioController(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pecs_keyboard);
		ViewGroup subjectsContainer = (ViewGroup) findViewById(R.id.subjectsContainer);
		List<Card> subjects = Card.byCategory(Category.getSubjects().getId());
		addCardsToContainer(subjects, subjectsContainer);

		ViewGroup verbsContainer = (ViewGroup) findViewById(R.id.verbsContainer);
		List<Card> verbs = Card.byCategory(Category.getVerbs().getId());
		addCardsToContainer(verbs, verbsContainer);

		ViewGroup objectsContainer = (ViewGroup) findViewById(R.id.objectsContainer);
		List<Card> objects = Card.byCategory(Category.getObjects().getId());
		addCardsToContainer(objects, objectsContainer);
	}

	private void addCardsToContainer(List<Card> cards, ViewGroup container) {
		container.removeAllViews();
		for (Card card : cards) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView v = (ImageView) inflater.inflate(R.layout.card, null);
			int w = (int) getResources().getDimension(R.dimen.card_image);
			v.setImageBitmap(BitmapFactory.decodeScaledByteArray(
					card.getPicture(), w, w));
			v.setTag(card);
			container.addView(v);
		}
	}

	public void selectCard(View v) {
		Card card = (Card) v.getTag();
		audioController.play(card.getSoundPath());
	}

}
