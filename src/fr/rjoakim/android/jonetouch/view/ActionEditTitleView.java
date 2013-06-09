package fr.rjoakim.android.jonetouch.view;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Strings;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.dialog.ResultView;
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
public abstract class ActionEditTitleView implements ResultView<String> {

	private final View view;
	private final ActionService actionService;
	private final Activity activity;

	public abstract void onSuccess(View view, String value);
	public abstract void onCancel(View view);
	
	public ActionEditTitleView(Activity activity, ActionService actionService) {
		this.activity = activity;
		this.actionService = actionService;
		this.view = activity.getLayoutInflater()
				.inflate(R.layout.action_detail_view_title_edit_text_layout, null);
	}
	
	public View build(final Action action) {
		final EditText editText = (EditText) view.findViewById(R.id.actionDetailViewTitleEditText);
		editText.setText(action.getTitle());
		
		final View button = view.findViewById(R.id.actionDetailViewTitleValidateButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Strings.isNullOrEmpty(editText.getText().toString())) {
					try {
						actionService.updateTitle(action.getId(), editText.getText().toString());
						onSuccess(editText.getText().toString());
					} catch (ServiceException e) {
						onFailed();
					}
				} else {
					Toast.makeText(v.getContext(),
							activity.getString(R.string.incorrect_data), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		View undo = view.findViewById(R.id.actionDetailViewTitleUndoButton);
		undo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancel(view);
			}
		});
		
		return view;
	}
	
	@Override
	public void onSuccess(String value) {
		onSuccess(view, value);
	}
}
