package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServiceException;

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
public abstract class DeleteActionMyDialog extends MyDialog<Void> {

	private final ActionService actionService;
	private final Action action;

	public DeleteActionMyDialog(Activity activity, ActionService actionService, Action action) {
		super(activity, R.layout.delete_action_my_dialog);
		this.actionService = actionService;
		this.action = action;

		TextView textView = (TextView) content.findViewById(R.id.deleteActionDialogTextView);
		textView.setText(action.getTitle());
	}

	@Override
	public String getTitleView(Activity activity) {
		return activity.getString(R.string.delete_action_dialog_title);
	}
	
	@Override
	public void onFailed() {
		dialog.dismiss();
		Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void initMyDialogLayout(View dialogRootView) {
		Button yes = (Button) dialogRootView.findViewById(R.id.myDialogPositiveButton);
		yes.setText((activity.getString(R.string.no_button)));
		
		Button no = (Button) dialogRootView.findViewById(R.id.myDialogNegativeButton);
		no.setText((activity.getString(R.string.yes_button)));
	}
	
	@Override
	public void onPositiveButton(View v) {
		try {
			actionService.remove(action);
			dialog.dismiss();
			onSuccess(null);
		} catch (ServiceException e) {
			onFailed();
		}
	}
	
	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
}
