package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.User;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.service.UserService;

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
public abstract class LogInDialog extends MyDialog<String> {

	public LogInDialog(final Activity activity) {
		super(activity, R.layout.log_in_dialog);
	}

	@Override
	protected String getTitleView(Activity activity) {
		return activity.getString(R.string.log_in_dialog_title);
	}
	
	@Override
	protected void onPositiveButton(View v) {
		if (!checkIfPasswordIsCorrect()) {
			Toast.makeText(activity,
					getString(R.string.log_in_dialog_pwd_wrong), Toast.LENGTH_LONG).show();
		} else {
			dialog.dismiss();
			onSuccess(getPasswordEditText());
		}
	}
	
	@Override
	protected void onNegativeButton(View v) {
		dialog.dismiss();
		onFailed();
	}
	
	private boolean checkIfPasswordIsCorrect() {
		final String pwd = getPasswordEditText();
		if (pwd != null && pwd.length() >= 6) {
			final UserService userService = new UserService(activity);
			try {
				User user = userService.get();
				String pwdEncode = userService.encodePassword(user.getEmail(), pwd);
				return user.getPassword().equals(pwdEncode);
			} catch (ServiceException e) {
				//
			}
		}
		return false;
	}

	private String getPasswordEditText() {
		EditText editText = (EditText) content.findViewById(R.id.editTextAuthAlertDialogPwd);
		return editText.getText().toString();
	}
}
