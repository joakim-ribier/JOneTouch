package fr.rjoakim.android.jonetouch.view;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.JOneTouchActivity;
import fr.rjoakim.android.jonetouch.MyTerminal;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.async.CommandExecutor;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.dialog.ChoiceConnectionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.DeleteActionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.UpdateActionMyDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
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
public class ActionView {

	private final JOneTouchActivity activity;
	private final ServerService serverService;
	private final MyTerminal myTerminal;
	private final MyAuthentication myAuthentication;
	private final ActionService actionService;
	
	private final LinearLayout view;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public ActionView(JOneTouchActivity activity, ServerService serverService,
			MyTerminal myTerminal, MyAuthentication myAuthentication, ActionService actionService) {
		
		this.activity = activity;
		this.serverService = serverService;
		this.myTerminal = myTerminal;
		this.myAuthentication = myAuthentication;
		this.actionService = actionService;
		
		this.view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.action_view_layout, null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			LayoutTransition layoutTransition = new LayoutTransition();
			layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
			this.view.setLayoutTransition(layoutTransition);
		}
	}

	public View buildEditView(Action action, int index) {
		return build(action, true, index);
	}

	public View build(Action action, int index) {
		return build(action, false, index);
	}
	
	private View build(final Action action, boolean edit, final int index) {
		final Server server = getServer(action);
		final View runScriptView = view.findViewById(R.id.actionLayoutWidgetStart);
		runScriptView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (server != null) {
					runScript(action, server);
				} else {
					ChoiceConnectionMyDialog choiceConnectionMyDialog = new ChoiceConnectionMyDialog(activity, serverService) {
						@Override
						public void onSuccess(Server value) {
							runScript(action, value);
						}
					};
					choiceConnectionMyDialog.show();
				}
			}
		});
		
		TextView titleTextView = (TextView) view.findViewById(R.id.actionLayoutViewTitleTextView);
		titleTextView.setText(action.getTitle());

		TextView descTextView = (TextView) view.findViewById(R.id.actionLayoutViewDescriptionTextView);
		descTextView.setText(action.getDescription());
		
		ImageView isServerConnectionImageDefine = (ImageView) view.findViewById(R.id.actionViewLayoutLinkImage);
		if (server != null) {
			isServerConnectionImageDefine.setBackgroundResource(R.drawable.link);
		}

		View isServerConnectionView = view.findViewById(R.id.actionViewLayoutLink);
		isServerConnectionView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (server != null) {
					Toast.makeText(v.getContext(),
							activity.getString(R.string.action_view_server_connection, server.getTitle()), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(v.getContext(),
							getString(R.string.action_view_layout_no_server_connection), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		View showActionDetailsViewButton = view.findViewById(R.id.actionViewLayoutDetails);
		showActionDetailsViewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.showAnimatorView(index);
			}
		});
		
		if (edit) {
			View optionsLayout = view.findViewById(R.id.actionViewLayoutOptionsLayout);
			optionsLayout.setVisibility(View.VISIBLE);
			setActionUpdateButtonEvent(action, index);
			setActionDeleteButtonEvent(action);
		}
		
		return view;
	}

	private Server getServer(Action action) {
		Long serverId = action.getServerId();
		if (serverId == null) {
			return null;
		}
		try {
			return serverService.get(serverId);
		} catch (ServiceException e) {
			return null;
		} 
	}
	
	private void runScript(final Action action, final Server server) {
		CommandExecutor execCommand = new CommandExecutor(
				activity, myTerminal, myAuthentication);
		execCommand.connect(server);
		execCommand.execute(action.formatScriptsForExecCMD());
	}
	
	private String getString(int id) {
		return activity.getString(id);
	}
	
	private void setActionUpdateButtonEvent(final Action action, final int index) {
		View actionUpdateButton = view.findViewById(R.id.actionViewLayoutOptionsLayoutUpdate);
		actionUpdateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateActionMyDialog updateActionMyDialog = new UpdateActionMyDialog(activity, serverService, actionService) {
					@Override
					public void onSuccess(Long t) {
						ActionView.this.activity.rebuildAllActionViews(index);
					}
				};
				updateActionMyDialog.show(action);
			}
		});
	}
	
	private void setActionDeleteButtonEvent(final Action action) {
		View deleteActionButton = view.findViewById(R.id.actionViewLayoutOptionsLayoutDelete);
		deleteActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteActionMyDialog deleteActionMyDialog = new DeleteActionMyDialog(activity, actionService, action) {
					@Override
					public void onSuccess(Void t) {
						ActionView.this.activity.rebuildAllActionViews(0);
					}
				};
				deleteActionMyDialog.show();
			}
		});
	}
}
