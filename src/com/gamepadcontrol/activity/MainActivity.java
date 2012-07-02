package com.gamepadcontrol.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gamepadcontrol.Gamepad;
import com.gamepadcontrol.GamepadKeyEvent;
import com.gamepadcontrol.GamepadKeyListener;
import com.gamepadcontrol.R;

public class MainActivity extends Activity {

	TextView showvalue;
	ScrollView scrollView;
	Gamepad pad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		showvalue = (TextView) findViewById(R.id.textview_main_showvalue);
		scrollView = (ScrollView) findViewById(R.id.scrollview_main);
		
		pad = Gamepad.getInstance();
		
		pad.setGamepadKeyListener(new GamepadKeyListener() {

			public void onKeyDown(GamepadKeyEvent event) {
				showvalue.append("Get KeyEvent " + event.getKey() + "\n");
				scrollView.smoothScrollBy(150, 150);
				Log.d("GamepadControl", event.getKey() + "");
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		pad.startListening();
		super.onResume();
	}

	@Override
	protected void onPause() {
		pad.stopListening();
		super.onPause();
	}
	
	
}
