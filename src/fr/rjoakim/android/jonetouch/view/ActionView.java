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

	public View buildEditView(final Action action) {
		return build(action, true);
	}

	public View build(final Action action) {
		return build(action, false);
	}
	
	private View build(final Action action, boolean edit) {
		final Server server = getServer(action);
		final ImageView runScriptView = (ImageView) view.findViewById(R.id.actionLayoutWidgetStart);
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
		
		TextView serverDefineTextView = (TextView) view.findViewById(R.id.actionLayoutWidgetServer);
		if (server != null) {
			serverDefineTextView.setText(
					activity.getString(R.string.action_view_server_connection, server.getTitle()));
		} else {
			serverDefineTextView.setText(getString(R.string.action_view_layout_no_server_connection));
		}
		
		if (edit) {
			setEditTitleButtonEvent(action);
			setEditDescriptionButtonEvent(action);
			setEditServerConnectionButtonEvent(action);
		}
		
		return view;
	}
	
	private void setEditTitleButtonEvent(final Action action) {
		View button = view.findViewById(R.id.actionViewEditTitleButton);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				v.setVisibility(View.INVISIBLE);
				final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionLayoutView);
				int indexOfChild = linearLayout.indexOfChild(view.findViewById(R.id.actionLayoutViewTitle));
				ActionEditTitleView actionEditTitleView = new ActionEditTitleView(activity, actionService) {
					@Override
					public void onSuccess(View child, String value) {
						activity.rebuildAllActionViews();
					}

					@Override
					public void onFailed() {
						Toast.makeText(v.getContext(),
								activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onCancel(View view) {
						((LinearLayout)view.getParent()).removeView(view);
						v.setVisibility(View.VISIBLE);
					}
				};
				linearLayout.addView(actionEditTitleView.build(action), indexOfChild+1);
			}
		});
	}
	
	private void setEditDescriptionButtonEvent(final Action action) {
		View button = view.findViewById(R.id.actionViewEditDescriptionButton);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				v.setVisibility(View.INVISIBLE);
				final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionLayoutView);
				int indexOfChild = linearLayout.indexOfChild(view.findViewById(R.id.actionLayoutViewDescription));
				ActionEditDescriptionView actionEditDescriptionView = new ActionEditDescriptionView(activity, actionService) {
					@Override
					public void onSuccess(View child, String value) {
						activity.rebuildAllActionViews();
					}

					@Override
					public void onFailed() {
						Toast.makeText(v.getContext(),
								activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onCancel(View view) {
						((LinearLayout)view.getParent()).removeView(view);
						v.setVisibility(View.VISIBLE);
					}
				};
				linearLayout.addView(actionEditDescriptionView.build(action), indexOfChild+1);
			}
		});
	}
	
	private void setEditServerConnectionButtonEvent(final Action action) {
		View button = view.findViewById(R.id.actionViewEditServerConnectionButton);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				v.setVisibility(View.INVISIBLE);
				final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionLayoutView);
				int indexOfChild = linearLayout.indexOfChild(view.findViewById(R.id.actionLayoutViewServerConnection));
				ActionEditServerConnectionView actionEditDescriptionView = new ActionEditServerConnectionView(activity, actionService, serverService) {
					@Override
					public void onSuccess(Void arg) {
						activity.rebuildAllActionViews();
					}

					@Override
					public void onFailed() {
						Toast.makeText(v.getContext(),
								activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onCancel(View view) {
						((LinearLayout)view.getParent()).removeView(view);
						v.setVisibility(View.VISIBLE);
					}
				};
				linearLayout.addView(actionEditDescriptionView.build(action), indexOfChild+1);
			}
		});
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
}
