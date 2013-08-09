package fr.rjoakim.android.jonetouch.dialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.async.RestoreBackupAsyncTask;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;

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
public abstract class ChoiceRestoreDataMyDialog extends MyDialog<Void> {

	private final ActionService actionService;
	private final ServerService serverService;
	private final MyAuthentication myAuthentication;

	private boolean isInProcess = false;
	
	public ChoiceRestoreDataMyDialog(Activity activity, MyAuthentication myAuthentication,
			ServerService serverService, ActionService actionService) {
		
		super(activity, R.layout.choice_restore_data_dialog);
		this.myAuthentication = myAuthentication;
		this.serverService = serverService;
		this.actionService = actionService;
		
		View pasteButton = content.findViewById(R.id.choiceRestoreViewPasteButton);
		pasteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String textFromClipboard = getTextFromClipboard();
				TextView backupContent = (TextView) content.
						findViewById(R.id.choiceRestoreViewBackupContentEditText);
				backupContent.setText(textFromClipboard);
			}
		});
		
		View trashButton = content.findViewById(R.id.choiceRestoreViewTrashButton);
		trashButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView backupContent = (TextView) content.
						findViewById(R.id.choiceRestoreViewBackupContentEditText);
				backupContent.setText("");
			}
		});
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.choice_restore_data_dialog_title);
	}

	@Override
	public void onPositiveButton(View v) {
		if (!isInProcess) {
			isInProcess = true;
			((ViewAnimator) content).showNext();
			
			TextView backupContent = (TextView) content.findViewById(
					R.id.choiceRestoreViewBackupContentEditText);
			
			EditText key = (EditText) content.findViewById(
					R.id.choiceRestoreViewPwdEditText);
			
			CheckBox isGetServers = (CheckBox) content.findViewById(
					R.id.choiceRestoreViewServersCheckBox);
			
			CheckBox isGetActions = (CheckBox) content.findViewById(
					R.id.choiceRestoreViewActionsCheckBox);
			
			CheckBox isResetAllDatas = (CheckBox) content.findViewById(
					R.id.choiceRestoreViewResetBeforeCheckBox);
			
			
			RestoreBackupAsyncTask restoreBackupAsyncTask = new RestoreBackupAsyncTask(
					this, myAuthentication, serverService, actionService) {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}
				
				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					setTextInfoWithTime("success", getString(R.string.choice_restore_data_dialog_end_info));
					if (!result.equals("success")) {
						Toast.makeText(activity, getString(R.string.set_clipboard_message), Toast.LENGTH_LONG).show();
						setClipboardError(result);
					}
					onSuccess(null);
				}
			};
			restoreBackupAsyncTask.execute(
					backupContent.getText().toString(),
					key.getText().toString(),
					String.valueOf(isGetServers.isChecked()),
					String.valueOf(isGetActions.isChecked()),
					String.valueOf(isResetAllDatas.isChecked()));
		} else {
			dialog.dismiss();
		}
	}

	private String formatStringWithTime(String value) {
		DateTimeFormatter DATE_FORMAT =
	            DateTimeFormat.forPattern("HH:mm:s:SSS");
		return DATE_FORMAT.print(DateTime.now()) + " : " + value;
	}
	
	public void setTextInfoWithoutTime(String type, String message) {
		if (type.equals("info")) {
			getUserInfoLayout().addView(createInfoTextViewWidget(message));
		} else {
			getUserInfoLayout().addView(createErrorTextViewWidget(message));
		}
	}
	
	public void setTextInfoWithTime(String type, String message) {
		if (type.equals("success")) {
			getUserInfoLayout().addView(
					createSuccessTextViewWidget(formatStringWithTime(message)));
		} else {
			if (type.equals("info")) {
				getUserInfoLayout().addView(
						createInfoTextViewWidget(formatStringWithTime(message)));
				
			} else {
				getUserInfoLayout().addView(
						createErrorTextViewWidget(formatStringWithTime(message)));
			}
		}
	}

	private LinearLayout getUserInfoLayout() {
		return (LinearLayout) content.findViewById(R.id.choiceRestoreViewBackupLogLayout);
	}
	
	private TextView createInfoTextViewWidget(String message) {
		return buildTextView(message, R.style.text_htmlgreen_12sp);
	}
	
	private TextView createErrorTextViewWidget(String message) {
		return buildTextView(message, R.style.text_htmlred_12sp);
	}
	
	private TextView createSuccessTextViewWidget(String message) {
		return buildTextView(message, R.style.text_htmlblue_12sp);
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
	
	@Override
	public void onNegativeButton(View v) {
		if (!isInProcess) {
			dialog.dismiss();
		} else {
			isInProcess = false;
			getUserInfoLayout().removeAllViewsInLayout();
			((ViewAnimator) content).showPrevious();
		}
	}
}
