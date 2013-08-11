package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import fr.rjoakim.android.jonetouch.R;

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
public abstract class HelpMyDialog extends MyDialog<Void> {

	public HelpMyDialog(final Activity activity) {
		super(activity, R.layout.help_my_dialog);
	}

	@Override
	public void onFailed() {}

	@Override
	public void onSuccess(Void t) {}

	@Override
	public String getTitleView(Activity activity) {
		return activity.getString(R.string.help_dialog_title);
	}
	
	@Override
	public void initMyDialogLayout(View myDialogLayout) {
		Button button = (Button) myDialogLayout.findViewById(R.id.myDialogNegativeButton);
		LinearLayout parent = (LinearLayout)button.getParent();
		parent.removeView(button);
	}

	@Override
	public void onPositiveButton(View v) {
		dialog.dismiss();
	}

	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
}
