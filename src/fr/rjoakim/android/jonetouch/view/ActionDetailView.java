package fr.rjoakim.android.jonetouch.view;

import java.util.List;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.JOneTouchActivity;
import fr.rjoakim.android.jonetouch.MyTerminal;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.dialog.DeleteActionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.DeleteActionScriptMyDialog;
import fr.rjoakim.android.jonetouch.dialog.UpdateActionScriptMyDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ScriptService;
import fr.rjoakim.android.jonetouch.service.ServerService;
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
public class ActionDetailView {

	private final JOneTouchActivity activity;
	private final ServerService serverService;
	private final MyTerminal myTerminal;
	private final MyAuthentication myAuthentication;
	private final ActionService actionService;
	private final ScriptService scriptService;
	
	private final View view;

	public ActionDetailView(JOneTouchActivity activity, OnTouchListener gestureListener, ServerService serverService,
			MyTerminal myTerminal, MyAuthentication myAuthentication, ActionService actionService, ScriptService scriptService) {

		this.activity = activity;
		this.serverService = serverService;
		this.myTerminal = myTerminal;
		this.myAuthentication = myAuthentication;
		this.actionService = actionService;
		this.scriptService = scriptService;
		
		this.view = activity.getLayoutInflater().inflate(R.layout.action_detail_view, null);
		this.view.findViewById(R.id.actionDetailViewScroll).setOnTouchListener(gestureListener);
	}
	
	public View build(Action action) {
		addActionView(action);
		addActionScriptView(action);
		return view;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addActionView(Action action) {
		ActionView actionView = new ActionView(
				activity, serverService, myTerminal, myAuthentication, actionService);
		
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionDetailViewLayout);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			LayoutTransition layoutTransition = new LayoutTransition();
			layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
			linearLayout.setLayoutTransition(layoutTransition);
		}
		linearLayout.addView(actionView.buildEditView(action));
	}

	private void addActionScriptView(final Action action) {
		setTitleActionScriptTextView(action);
		setEditScriptActionButton(action);
		setDeleteActionButtonEvent(action);
		
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionDetailScriptViewLayout);
		if (action.getActionScripts().size() > 0) {
			for (final ActionScript actionScript: action.getActionScripts()) {
				final LinearLayout actionDetailScriptView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.action_detail_view_script_layout, null);
				
				TextView textView = (TextView) actionDetailScriptView.findViewById(R.id.actionDetailScriptTextView);
				textView.setText(actionScript.getScript());
				
				addDeleteScriptButtonEvent(actionScript, actionDetailScriptView);
				linearLayout.addView(actionDetailScriptView);
			}
		} else {
			TextView titleScript = (TextView) view.findViewById(R.id.actionDetailScriptTitleTextView);
			titleScript.setText(activity.getString(
					R.string.add_action_script_dialog_title));
		}
	}

	private void setTitleActionScriptTextView(final Action action) {
		TextView titleScript = (TextView) view.findViewById(R.id.actionDetailScriptTitleTextView);
		titleScript.setText(activity.getString(
				R.string.action_detail_view_script_title_number,
				String.valueOf(action.getActionScripts().size())));
	}
	
	private void setEditScriptActionButton(final Action action) {
		View editScriptButton = view.findViewById(R.id.actionDetailScriptViewEditButton);
		editScriptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateActionScriptMyDialog updateActionScriptMyDialog = 
						new UpdateActionScriptMyDialog(activity, scriptService) {
					@Override
					public void onSuccess(List<String> values) {
						try {
							actionService.updateActionScripts(action.getId(), action.getActionScripts(), values);
							ActionDetailView.this.activity.rebuildAllActionViews();
						} catch (ServiceException e) {
							Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
						}
					}
				};
				updateActionScriptMyDialog.show(action);
			}
		});
	}

	private void setDeleteActionButtonEvent(final Action action) {
		View deleteActionButton = view.findViewById(R.id.actionDetailViewDeleteButton);
		deleteActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteActionMyDialog deleteActionMyDialog = new DeleteActionMyDialog(activity, actionService, action) {
					@Override
					public void onSuccess(Void t) {
						ActionDetailView.this.activity.rebuildAllActionViews(0);
					}
				};
				deleteActionMyDialog.show();
			}
		});
	}
	
	private void addDeleteScriptButtonEvent(final ActionScript actionScript,
			final LinearLayout actionDetailScriptView) {
		
		View deleteScriptButton = actionDetailScriptView.findViewById(R.id.actionDetailScriptDeleteButton);
		deleteScriptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteActionScriptMyDialog deleteActionScriptMyDialog = new DeleteActionScriptMyDialog(activity, actionService, actionScript) {
					@Override
					public void onSuccess(Void t) {
						ActionDetailView.this.activity.rebuildAllActionViews();
					}

					@Override
					public void onFailed() {
						Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
					}
				};
				deleteActionScriptMyDialog.show();
			}
		});
	}
}
