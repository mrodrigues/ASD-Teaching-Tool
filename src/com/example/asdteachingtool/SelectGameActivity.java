package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class SelectGameActivity extends Activity {
	
	private static Context initialContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_game);
		initialContext = this;
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
	
	public static Context getContext() {
		return initialContext;
	}
}
