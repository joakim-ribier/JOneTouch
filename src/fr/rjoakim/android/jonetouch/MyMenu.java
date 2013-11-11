package fr.rjoakim.android.jonetouch;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import fr.rjoakim.android.jonetouch.async.CommandExecutor;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.dialog.AddActionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.AddServerConnectionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.ChoiceBackupDataMyDialog;
import fr.rjoakim.android.jonetouch.dialog.ChoiceRestoreDataMyDialog;
import fr.rjoakim.android.jonetouch.dialog.DeleteServerConnectionMyDialog;
import fr.rjoakim.android.jonetouch.dialog.HelpMyDialog;
import fr.rjoakim.android.jonetouch.dialog.UpdateServerConnectionMyDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.AuthenticationTypeService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.SharePreferencesUtils;

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
public class MyMenu {

	private final MyAuthentication myAuthentication;

	private final SlidingMenu slidingMenu;
	private final JOneTouchActivity activity;
	private final View root;
	
	private MyTerminal myTerminal;
	
	private final ServerService serverService;
	private final AuthenticationTypeService authenticationTypeService;
	private final ActionService actionService;
	
	public MyMenu(JOneTouchActivity activity, MyAuthentication myAuthentication, ServerService serverService,
			AuthenticationTypeService authenticationTypeService, ActionService actionService) {
		
		this.activity = activity;
		this.serverService = serverService;
		this.authenticationTypeService = authenticationTypeService;
		this.myAuthentication = myAuthentication;
		this.actionService = actionService;
		this.slidingMenu = new SlidingMenu(activity);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.my_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.my_menu);
		this.root = slidingMenu.getMenu();
		
		configureMenu();
		rebuildAllServerConnections();
	}
	
	private void configureMenu() {
		final View addOrUpdateActionLayout = root.findViewById(R.id.relativeLayoutActionAdd);
		addOrUpdateActionLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayAddOrUpdateActionDialog();
			}
		});
		
		final View addConnectionLayout = root.findViewById(R.id.layoutLeftMenuAddConnection);
		addConnectionLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayAddConnectionDialog();
			}
		});
		
		final View exportLayout = root.findViewById(R.id.myMenuBackupLayout);
		exportLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayBackupDialog();
			}
		});
		
		final View restoreLayout = root.findViewById(R.id.myMenuRestorationLayout);
		restoreLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayRestoreDialog();
			}
		});
		
		final View helpLayout = root.findViewById(R.id.myMenuHelpLayout);
		helpLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayHelpDialog();
			}
		});
		
		final View lockOrUnlockLayout = root.findViewById(R.id.myMenuViewLockOrUnLockLayout);
		lockOrUnlockLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SharePreferencesUtils.isAppLocked(activity)) {
					SharePreferencesUtils.setKey(activity, myAuthentication.getKey());
				} else {
					SharePreferencesUtils.setAppLocked(activity);
				}
				displayLockOrUnlockItemMenu();
			}
		});
		
		final View logout = root.findViewById(R.id.myMenuViewLogoutLayout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		
		final View aboutOf = root.findViewById(R.id.myMenuViewAboutOfLayout);
		aboutOf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.startActivity(new Intent(activity, AboutOfActivity.class));
			}
		});
	}

	private void displayRestoreDialog() {
		ChoiceRestoreDataMyDialog choiceRestoreDataMyDialog = new ChoiceRestoreDataMyDialog(
				activity, myAuthentication, serverService, actionService) {
					@Override
					public void onSuccess(Void t) {
						rebuildAllServerConnections();
						MyMenu.this.activity.rebuildAllActionViews(0);
					}
		};
		choiceRestoreDataMyDialog.show();
	}

	private void displayBackupDialog() {
		ChoiceBackupDataMyDialog choiceBackupDataMyDialog = new ChoiceBackupDataMyDialog(
				activity, myAuthentication, serverService, actionService) {
		};
		choiceBackupDataMyDialog.show();
	}
	
	private void displayAddOrUpdateActionDialog() {
		final AddActionMyDialog myDialog = new AddActionMyDialog(
				activity, serverService, actionService) {
			@Override
			public void onSuccess(Long id) {
				MyMenu.this.activity.rebuildAllActionViewsAndSetIndexOnLastCreate();
				hide();
			}
		};
		myDialog.show();
	}
	
	private void displayAddConnectionDialog() {
		final AddServerConnectionMyDialog myDialog =
				new AddServerConnectionMyDialog(activity, serverService, authenticationTypeService, myAuthentication) {
			@Override
			public void onSuccess(Long id) {
				rebuildAllServerConnections();
				MyMenu.this.activity.rebuildAllActionViews(0);
			}
		};
		myDialog.show();
	}
	
	public void displayHelpDialog() {
		HelpMyDialog helpDialog = new HelpMyDialog(activity) {};
		helpDialog.show();
	}

	public void rebuildAllServerConnections() {
		final LinearLayout layoutContainsServerItems = (LinearLayout) root.findViewById(R.id.myMenuLayoutContainsServersItem);
		layoutContainsServerItems.removeAllViewsInLayout();
		try {
			List<Server> servers = serverService.list();
			if (servers.size() > 0) {
				for (final Server server: servers) {
					View view = createServerView(server);
					layoutContainsServerItems.addView(view);
				}
			} else {
				View view = activity.getLayoutInflater().inflate(R.layout.my_menu_no_server_connection_layout, null);
				layoutContainsServerItems.addView(view);
			}
		} catch (ServiceException e) {
			Toast.makeText(activity, activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}

	private View createServerView(final Server server) {
		View view = activity.getLayoutInflater().inflate(R.layout.my_menu_server_connection_layout, null);
		TextView titleTextView = (TextView) view.findViewById(R.id.myMenuItemConnectionTitleTextView);
		titleTextView.setText(server.getTitle());
		addRemoveServerConnectionOnClick(server,
				view.findViewById(R.id.myMenuItemServerConnectionDeleteClick));
		addUpdateServerConnectionOnClick(server,
				view.findViewById(R.id.myMenuItemServerConnectionUpdateClick));
		addPingServerConnectionOnClick(server,
				view.findViewById(R.id.myMenuItemConnectionTerminalLayoutClick));
		return view;
	}

	private void addPingServerConnectionOnClick(final Server server, final View view) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommandExecutor execCommand = new CommandExecutor(activity, myTerminal, myAuthentication);
				execCommand.connect(server);
				execCommand.execute("hostname");
			}
		});
	}
	
	private void addRemoveServerConnectionOnClick(final Server server, final View view) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteServerConnectionMyDialog deleteServerConnectionMyDialog = new DeleteServerConnectionMyDialog(activity, server) {
					@Override
					public void onSuccess(Void t) {
						Toast.makeText(activity,
								activity.getString(R.string.delete_server_connection_my_dialog_success), Toast.LENGTH_LONG).show();
						rebuildAllServerConnections();
						MyMenu.this.activity.rebuildAllActionViews(0);
					}
					
					@Override
					public void onFailed() {
						Toast.makeText(activity,
								activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
					}
				};
				deleteServerConnectionMyDialog.show();
			}
		});
	}
	
	private void addUpdateServerConnectionOnClick(final Server server, final View updateServerButton) {
		updateServerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final UpdateServerConnectionMyDialog updateConnectionDialog =
						new UpdateServerConnectionMyDialog(activity, serverService, authenticationTypeService, server, myAuthentication) {
					@Override
					public void onSuccess(Long id) {
						rebuildAllServerConnections();
						MyMenu.this.activity.rebuildAllActionViews(0);
					}
				};
				updateConnectionDialog.show();
			}
		});
	}

	public void show() {
		myTerminal.hide();
		slidingMenu.showMenu();
	}
	
	public void hideOrShow() {
		if (slidingMenu.isMenuShowing()) {
			hide();
		} else {
			show();
		}
	}
	
	public void hide() {
		slidingMenu.showContent();
	}

	public void initTerminal(MyTerminal myTerminal) {
		this.myTerminal = myTerminal;
	}

	public void displayLockOrUnlockItemMenu() {
		ImageView lockOrUnlockImageView = (ImageView) root.findViewById(R.id.myMenuViewLockOrUnLockImageView);
		TextView lockOrUnlockTextView = (TextView) root.findViewById(R.id.myMenuViewLockOrUnLockTextView);
		if (SharePreferencesUtils.isAppLocked(activity)) {
			lockOrUnlockTextView.setText(activity.getString(R.string.my_menu_unlock));
			lockOrUnlockImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.unlock));
		} else {
			lockOrUnlockTextView.setText(activity.getString(R.string.my_menu_lock));
			lockOrUnlockImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.lock));
		}
	}
}
