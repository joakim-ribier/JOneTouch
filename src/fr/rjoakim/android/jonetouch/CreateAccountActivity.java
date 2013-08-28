package fr.rjoakim.android.jonetouch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.dialog.HelpMyDialog;
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
public class CreateAccountActivity extends MyActivity implements OnClickListener {

	private final static int PASSWORD_LENGTH = 8;
	private Button button;
	private UserService userService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_activity);

		button = (Button) findViewById(R.id.buttonLoginDisplay);
		button.setOnClickListener(this);
		
		userService = new UserService(getApplicationContext());
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.create_account_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_options_help:
			HelpMyDialog helpDialog = new HelpMyDialog(this) {};
			helpDialog.show();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			final EditText editTextEmail = (EditText) findViewById(R.id.editTextLoginDisplayEmail);
			final EditText editTextPwd = (EditText) findViewById(R.id.editTextLoginDisplayPwd);
			String email = editTextEmail.getText().toString();
			if (isEmailValid(email)) {
				String password = editTextPwd.getText().toString();
				if (password != null && password.length() >= PASSWORD_LENGTH) {
					try {
						Long id = userService.create(email, password);
						if (id != null) {
							Intent intent = new Intent(this, JOneTouchActivity.class);
							intent.putExtra("user_pwd", password);
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(this, getString(R.string.create_account_failed), Toast.LENGTH_SHORT)
							.show();
						}
					} catch (ServiceException e) {
						Toast.makeText(this, getString(R.string.create_account_failed), Toast.LENGTH_SHORT)
						.show();
					}
				} else {
					Toast.makeText(this, getString(R.string.create_account_error_validation_pwd), Toast.LENGTH_LONG)
					.show();
				}
			} else {
				Toast.makeText(this, getString(R.string.create_account_error_validation_email), Toast.LENGTH_LONG)
				.show();
			}
		}
	}

	private boolean isEmailValid(CharSequence email) {
		if (email == null) {
			return false;
		}
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
}
