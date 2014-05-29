package com.example.asdteachingtool;

import com.example.asdteachingtool.models.Question;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class SelectGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_game, menu);
		return true;
	}
	
	public void startQuestionsMenu(View v) {
		startActivity(new Intent(this, StartQuestionsMenuActivity.class));
	}
	
	public void startPecsMenu(View v) {
		startActivity(new Intent(this, StartPecsMenuActivity.class));
	}
}
