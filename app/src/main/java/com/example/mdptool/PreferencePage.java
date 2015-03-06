package com.example.mdptool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class PreferencePage extends Activity {
	
	public static final String F1 = "F1Key"; 
	public static final String F2 = "F2Key"; 
	SharedPreferences prefs;
	EditText editTextF1;
	EditText editTextF2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_page);
		
		prefs = this.getSharedPreferences(
			      "com.example.mdptool", Context.MODE_PRIVATE);
		editTextF1 = (EditText)findViewById(R.id.editText_F1);
		editTextF2 = (EditText)findViewById(R.id.editText_F2);
		
		editTextF1.setText(prefs.getString(F1, ""));
		editTextF2.setText(prefs.getString(F2, ""));
	}
	
	public void savePreference(View view){
		Editor edit = prefs.edit();
		edit.putString(F1, editTextF1.getText().toString());
		edit.putString(F2, editTextF2.getText().toString());
		edit.commit();
		this.finish();
	}
}
