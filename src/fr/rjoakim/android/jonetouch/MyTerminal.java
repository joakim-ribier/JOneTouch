package fr.rjoakim.android.jonetouch;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class MyTerminal {

	private final SlidingMenu slidingMenu;
	private final Activity activity;
	private final ScrollView content;
	private final LinearLayout layoutToAddItems;
	private MyMenu myMenu;

	public MyTerminal(Activity activity) {
		this.activity = activity;
		this.slidingMenu = new SlidingMenu(activity);
		slidingMenu.setMode(SlidingMenu.RIGHT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.my_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(R.layout.command_executor_activity);
		
		this.content = (ScrollView) slidingMenu.getMenu();
		this.layoutToAddItems = (LinearLayout) content.findViewById(R.id.mainActiviyTerminal);
	}

	public void show() {
		myMenu.hide();
		layoutToAddItems.removeAllViewsInLayout();
		slidingMenu.showMenu();
	}

	public TextView buildErrorTextView(String message) {
		return buildTextView(message, R.style.text_red_overlay_12sp);
	}
	
	public TextView buildInfoTextView(String message) {
		return buildTextView(message, R.style.text_green_overlay_12sp);
	}
	
	private TextView buildTextView(String message, int styleResId) {
		TextView textView = new TextView(activity.getApplicationContext());
		textView.setText(message);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(1, 1, 1, 1);
		textView.setLayoutParams(llp);
		textView.setTextAppearance(activity.getApplicationContext(), styleResId);
		textView.setSelected(true);
		return textView;
	}

	public void addInfo(String message) {
		layoutToAddItems.addView(buildInfoTextView(message));
	}

	public void addError(String message) {
		layoutToAddItems.addView(buildErrorTextView(message));
	}

	public void smoothScrollTo() {
		content.smoothScrollTo(0, layoutToAddItems.getBottom());
	}

	public void initMenu(MyMenu myMenu) {
		this.myMenu = myMenu;
	}
	
	public void hide() {
		slidingMenu.showContent();
	}
}
