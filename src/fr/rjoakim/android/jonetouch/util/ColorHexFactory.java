package fr.rjoakim.android.jonetouch.util;

import fr.rjoakim.android.jonetouch.R;

public class ColorHexFactory {

	public static int hexColorToInt(String hexColor) {
		if (hexColor.equals("#33B5E5")) {
			return R.color.action_background_choice_blue;
		}
		if (hexColor.equals("#AA66CC")) {
			return R.color.action_background_choice_purple;
		}
		if (hexColor.equals("#99CC00")) {
			return R.color.action_background_choice_green;
		}
		if (hexColor.equals("#FFBB33")) {
			return R.color.action_background_choice_orange;
		}
		if (hexColor.equals("#FF4444")) {
			return R.color.action_background_choice_red;
		}
		
		return R.color.action_background_choice_green;
	}
}
