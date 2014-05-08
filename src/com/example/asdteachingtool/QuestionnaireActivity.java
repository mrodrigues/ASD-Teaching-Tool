package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Questionnaire;

public class QuestionnaireActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.asdteachingtool.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire);
		// ---- TEST SAMPLE ------
		Questionnaire questionnaire = new Questionnaire();
		questionnaire.title = "Feliz";
		questionnaire.save();
		
		Option option1 = new Option();
		option1.questionnaire = questionnaire;
		option1.text = "Rosto sorridente";
		option1.correct = true;
		option1.save();

		Option option2 = new Option();
		option2.questionnaire = questionnaire;
		option2.text = "Rosto triste";
		option2.correct = false;
		option2.save();
		// -----------------------
		
		TextView questionnaireView = (TextView) findViewById(R.id.questionnaire_view);
		questionnaireView.setTextSize(40);
		questionnaireView.setText(questionnaire.title);
		
		ViewGroup optionsContainer = (ViewGroup) findViewById(R.id.options_container);
		optionsContainer.removeAllViews();
		for (Option option : questionnaire.options()) {
			Button optionView = new Button(this);
			optionView.setTextSize(20);
			optionView.setText(option.text);
			optionView.setId(option.getId().intValue());
			optionView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Button button = (Button) v;
					button.setTextColor(Color.RED);
					
					Option option = Option.load(Option.class, button.getId());
					if (option.correct) {
						button.setText("ACERTOU!");
					}
				}
			});
			optionView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			optionsContainer.addView(optionView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questionnaire, menu);
		return true;
	}

	public void selectOption(View view) {
		
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
//		EditText editText = (EditText) findViewById(R.id.edit_message);
//		String message = editText.getText().toString();
//		SharedPreferences pref = getSharedPreferences("a", MODE_PRIVATE);
		//intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

}
