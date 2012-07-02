package com.gamepadcontrol;

import java.util.ArrayList;

public class GamepadKeyEvent {
	ArrayList<Integer> keys;
	
	public GamepadKeyEvent() {
		keys = new ArrayList<Integer>();
	}
	
	protected void putEvent(int key) {
		keys.add(key);
	}
	
	public int getKey(int index) {
		return keys.get(index);
	}
	
	public int getKey() {
		return keys.get(0);
	}
	
	public int length() {
		return keys.size();
	}
}
