package fr.rjoakim.android.jonetouch.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.util.APIUtils;

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
public abstract class MyDialog<T> implements ResultView<T> {

	protected final Dialog dialog;
	protected final View content;
	protected final Activity activity;

	protected abstract void onPositiveButton(View v);
	protected abstract void onNegativeButton(View v);
	protected abstract String getTitleView(Activity activity);
	
	protected void initMyDialogLayout(View myDialogLayout) {}
	
	protected MyDialog(final Activity activity, final int resource) {
		this.activity = activity;
		this.content = activity.getLayoutInflater().inflate(resource, null);;
		
		final View myDialogLayout = activity.getLayoutInflater().inflate(R.layout.my_dialog, null);
		final ScrollView view = (ScrollView) myDialogLayout.findViewById(R.id.myDialogContentScrollView);
		view.addView(content);
		
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(myDialogLayout);
		dialog.setCancelable(false);
		
		final Button buttonOk = (Button) myDialogLayout.findViewById(R.id.myDialogPositiveButton);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPositiveButton(v);
			}
		});
		
		final View buttonClose = myDialogLayout.findViewById(R.id.myDialogNegativeButton);
		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNegativeButton(v);
			}
		});
		
		final TextView titleView = (TextView) myDialogLayout.findViewById(R.id.myDialogTitleTextView);
		titleView.setText(getTitleView(activity));

		initMyDialogLayout(myDialogLayout);
	}
	
	protected void defineTextToPositiveButton(String text) {
		Button buttonOk = (Button) dialog.findViewById(R.id.myDialogPositiveButton);
		buttonOk.setText(text);
	}
	
	protected void defineTextToExtraButton(String text) {
		View extraLayout = dialog.findViewById(R.id.myDialogButtonExtraLayout);
		extraLayout.setVisibility(View.VISIBLE);
		
		Button extraButton = (Button) dialog.findViewById(R.id.myDialogExtraButton);
		extraButton.setText(text);
		extraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onExtraButton(v);
			}
		});
	}
	
	public void show() {
		dialog.show();
	}
	
	public String getString(int resource) {
		return activity.getString(resource);
	}
	
	public String getString(int resource, Object... objects) {
		return activity.getString(resource, objects);
	}
	
	protected String getEditTextValue(int id) {
		final EditText editText = (EditText) content.findViewById(id);
		return editText.getText().toString();
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public String getTextFromClipboard() {
		if (APIUtils.ifAvailableAPI(Build.VERSION_CODES.HONEYCOMB)) {
			ClipboardManager clipboard = (ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			
			ClipData primaryClip = clipboard.getPrimaryClip();
			if (primaryClip != null) {
				ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
				return item.getText().toString();
			}
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null && clipboard.getText() != null) {
				return clipboard.getText().toString();
			}
		}
		return "";
	}
	
	public void setClipboardError(String message) {
		setClipboardMessage("error-message", message);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setClipboardMessage(String label, String message) {
		if (APIUtils.ifAvailableAPI(Build.VERSION_CODES.HONEYCOMB)) {
			ClipboardManager clipboard = (ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText(label, message);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(message);
		}
	}
	
	protected void onExtraButton(View v) {}
}
