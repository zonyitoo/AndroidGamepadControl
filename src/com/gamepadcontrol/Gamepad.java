package com.gamepadcontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Gamepad {
	
	public static final int KEY_TRIANGLE = 304;
	public static final int KEY_CIRCLE = 305;
	public static final int KEY_CROSS = 306;
	public static final int KEY_SQUARE = 307;
	public static final int KEY_LEFT_1 = 308;
	public static final int KEY_LEFT_2 = 310;
	public static final int KEY_RIGHT_1 = 309;
	public static final int KEY_RIGHT_2 = 311;
	public static final int KEY_SELECT = 312;
	public static final int KEY_START = 313;
	
	public static final int KEY_UP = 18;
	public static final int KEY_DOWN = 17;
	public static final int KEY_LEFT = 16;
	public static final int KEY_RIGHT = 15;
	
	static {
		// 已成功获取Root权限
		try {
			Process prs = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(prs.getOutputStream());
			
			os.writeBytes("chmod 666 /dev/input/event4\n");
			os.writeBytes("chmod 666 /dev/input/event2\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.loadLibrary("gpcnt");
	}
	/*
	public class InputData {
		public int type1, code1, value1;
		public int type2, code2, value2;
		public int type3, code3, value3;
		public InputData() {}
	}
	*/
	public native String readData();
	
	static Timer timer;
	static TimerTask timertask;
	static Gamepad instance = null;
	
	private JoystickListener joystickListener = null;
	private GamepadKeyListener keyListener = null;
	private GamepadMultikeyListener multikeyListener = null;
	
	private Handler eventHandler;
	
	private Gamepad() {
		eventHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String get = msg.getData().getString("ret");
				
				try {
					JSONObject ret = new JSONObject(get);
					GamepadKeyEvent keyevent = null;
					if (keyListener != null)
						keyevent = new GamepadKeyEvent();
					JoystickEvent jsevent = null;
					
					JSONArray arr = ret.getJSONArray("data");
					for (int i = 0; i < arr.length(); ++ i) {
						JSONObject cur = arr.getJSONObject(i);
						
						// Normal Key
						if (cur.getInt("type") == 1 && keyevent != null) {
							// On key down
							if (cur.getInt("value") == 1) {
								keyevent.putEvent(cur.getInt("code"));
							}
							// On key Release
							else {
								
							}
						}
						// JoyStick Key
						else if (cur.getInt("type") == 3 
								&& (cur.getInt("code") == 16 || cur.getInt("code") == 17)
								&& keyevent != null) {
							if (cur.getInt("code") == 17) {
								if (cur.getInt("value") == -1)
									keyevent.putEvent(KEY_UP);
								else if (cur.getInt("value") == 1)
									keyevent.putEvent(KEY_DOWN);
								else {
									
								}
							}
							else if (cur.getInt("code") == 16) {
								if (cur.getInt("value") == -1)
									keyevent.putEvent(KEY_LEFT);
								else if (cur.getInt("value") == 1)
									keyevent.putEvent(KEY_RIGHT);
								else {
									
								}
							}
						}
						// JoyStick
						else if (joystickListener != null){
							double sita;
						}
						
						if (keyListener != null && keyevent.length() == 1)
							keyListener.onKeyDown(keyevent);
						else if (multikeyListener != null && keyevent.length() > 1)
							multikeyListener.onMultikeyDown(keyevent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		};
	}
	
	public static Gamepad getInstance() {
		if (instance == null)
			instance = new Gamepad();
		return instance;
	}
	
	public void startListening() {
		timer = new Timer();
		timertask = new TimerTask() {
			
			@Override
			public void run() {
				try {
					String get = readData();
					JSONObject ret = new JSONObject(get);
					Log.d("GamepadControl", ret.toString());
					
					int num = ret.getInt("retcode");
					if (num == -1) return;
					
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("ret", get);
					msg.setData(bundle);
					eventHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		};
		
		timer.schedule(timertask, 0, 50);
	}
	
	public void stopListening() {
		timer.cancel();
	}
	
	public void setGamepadKeyListener(GamepadKeyListener listener) {
		keyListener = listener;
	}
	
	public class GamepadException extends Exception {

		public GamepadException() {
			super();
		}
		
		public GamepadException(String msg) {
			super(msg);
		}
		
	}
	
}
